package com.inokey.solution.dnk.nucleus.observability.autoconfigure

import com.inokey.solution.dnk.nucleus.observability.context.NucleusContextKeys
import com.inokey.solution.dnk.nucleus.observability.idempotency.NucleusIdempotencyDisabledException
import com.inokey.solution.dnk.nucleus.observability.idempotency.NucleusIdempotencyExecutor
import com.inokey.solution.dnk.nucleus.observability.idempotency.NucleusIdempotencyResult
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.context.ContextView
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

/**
 * Aspect canonique de [NucleusOp].
 *
 * Une méthode publique produit une seule observation. Pour une méthode `Mono`,
 * la politique d'idempotence est appliquée avant la souscription au métier.
 * Les fonctions Kotlin `suspend` historiques restent observées lorsque leur
 * politique d'idempotence vaut [NucleusIdempotencyMode.NONE].
 */
@Aspect
class NucleusOpAspectProFixedV2(
    private val observationRegistry: ObservationRegistry,
    private val idempotencyExecutor: NucleusIdempotencyExecutor?,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        val STANDARD_TAG_KEYS: Set<String> = setOf(
            "endpoint",
            "context",
            "user_id",
            "error_type",
        )
    }

    @Suppress("ReactiveStreamsUnusedPublisher", "UNCHECKED_CAST")
    @Around("@annotation(com.inokey.solution.dnk.nucleus.observability.autoconfigure.NucleusOp)")
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as? MethodSignature
            ?: return joinPoint.proceed()
        val annotation = signature.method.getAnnotation(NucleusOp::class.java)
            ?: return joinPoint.proceed()
        val operation = annotation.value
        val contextName = extractContextFromClass(joinPoint)
        val extraTags = parseExtraTags(annotation.extraTags, contextName)

        if (signature.method.parameterTypes.lastOrNull() == Continuation::class.java) {
            require(annotation.idempotency == NucleusIdempotencyMode.NONE) {
                "Idempotent Nucleus operations must return Mono: " +
                    "${signature.declaringTypeName}.${signature.name}"
            }
            return interceptSuspend(
                joinPoint = joinPoint,
                operation = operation,
                correlationMode = annotation.correlation,
                extraTags = extraTags,
            )
        }

        val result = joinPoint.proceed()
        return when (result) {
            is Mono<*> -> {
                val source = result as Mono<Any>
                val execution = when (annotation.idempotency) {
                    NucleusIdempotencyMode.NONE -> source.map { value ->
                        NucleusIdempotencyResult(value = value, replay = false)
                    }

                    else -> {
                        val executor = idempotencyExecutor
                            ?: return Mono.error<Any>(
                                NucleusIdempotencyDisabledException(operation.name),
                            )
                        executor.execute(
                            operation = operation,
                            mode = annotation.idempotency,
                            arguments = joinPoint.args,
                            sourceSupplier = { source },
                        )
                    }
                }

                execution.observeNucleusOperation(
                    operation = operation,
                    registry = observationRegistry,
                    correlationMode = annotation.correlation,
                    idempotencyMode = annotation.idempotency,
                    extraTags = extraTags,
                )
            }

            is Flux<*> -> {
                val source = result as Flux<Any>
                if (annotation.idempotency != NucleusIdempotencyMode.NONE) {
                    Flux.error<Any>(
                        IllegalStateException(
                            "Idempotent Nucleus operations must return Mono: " +
                                "${signature.declaringTypeName}.${signature.name}",
                        ),
                    )
                } else {
                    source.observedProV2(
                        operation = operation,
                        registry = observationRegistry,
                        correlationMode = annotation.correlation,
                        extraTags = extraTags,
                    )
                }
            }

            else -> {
                log.warn(
                    "@NucleusOp ignored on non-reactive method {}.{} returning {}",
                    signature.declaringTypeName,
                    signature.name,
                    result?.javaClass?.name ?: "null",
                )
                result
            }
        }
    }

    private fun interceptSuspend(
        joinPoint: ProceedingJoinPoint,
        operation: MultiplannerOperation,
        correlationMode: NucleusCorrelationMode,
        extraTags: Map<String, String>,
    ): Any? {
        val arguments = joinPoint.args
        @Suppress("UNCHECKED_CAST")
        val original = arguments.last() as Continuation<Any?>
        val reactorContext = reactorContextFrom(original.context)
        val correlationId = reactorContext
            ?.getOrEmpty<String>(NucleusContextKeys.CORRELATION_ID)
            ?.orElse("unknown")
            ?: "unknown"

        if (
            correlationMode == NucleusCorrelationMode.PROPAGATE_OR_GENERATE &&
            correlationId == "unknown"
        ) {
            log.warn(
                "Nucleus correlation context missing for suspend operation={}",
                operation.name,
            )
        }

        val observation = newObservation(
            operation = operation,
            registry = observationRegistry,
            idempotencyMode = NucleusIdempotencyMode.NONE,
            extraTags = extraTags,
        )
        val startedAt = System.nanoTime()
        val completed = AtomicBoolean(false)

        fun complete(error: Throwable?) {
            if (!completed.compareAndSet(false, true)) return
            if (error == null) {
                observation.lowCardinalityKeyValue("outcome", "SUCCESS")
                log.debug(
                    "NUCLEUS_SUCCESS operation={} correlationId={} replay=false",
                    operation.name,
                    correlationId,
                )
            } else {
                observation.lowCardinalityKeyValue("outcome", "ERROR")
                observation.error(error)
                log.error(
                    "NUCLEUS_ERROR operation={} correlationId={} exceptionType={}",
                    operation.name,
                    correlationId,
                    error.javaClass.simpleName,
                )
            }
            observation.lowCardinalityKeyValue("replay", "false")
            observation.stop()
            val durationMs = (System.nanoTime() - startedAt) / 1_000_000
            log.debug(
                "NUCLEUS_END operation={} correlationId={} durationMs={} replay=false",
                operation.name,
                correlationId,
                durationMs,
            )
        }

        log.debug(
            "NUCLEUS_START operation={} correlationId={} idempotencyMode=NONE",
            operation.name,
            correlationId,
        )

        val wrapped = object : Continuation<Any?> {
            override val context: CoroutineContext = original.context

            override fun resumeWith(result: Result<Any?>) {
                complete(result.exceptionOrNull())
                original.resumeWith(result)
            }
        }

        val replaced = arguments.copyOf()
        replaced[replaced.lastIndex] = wrapped

        return try {
            val returned = joinPoint.proceed(replaced)
            if (returned !== COROUTINE_SUSPENDED) complete(null)
            returned
        } catch (error: Throwable) {
            complete(error)
            throw error
        }
    }

    private fun reactorContextFrom(coroutineContext: CoroutineContext): ContextView? {
        var reactorContext: ContextView? = null
        coroutineContext.fold(Unit) { _, element ->
            if (element.javaClass.name == "kotlinx.coroutines.reactor.ReactorContext") {
                reactorContext = runCatching {
                    element.javaClass.methods
                        .firstOrNull { method ->
                            method.name == "getContext" && method.parameterCount == 0
                        }
                        ?.invoke(element) as? ContextView
                }.getOrNull()
            }
        }
        return reactorContext
    }

    private fun parseExtraTags(
        values: Array<String>,
        contextName: String,
    ): Map<String, String> {
        val tags = values.mapNotNull { value ->
            val separator = value.indexOf('=')
            if (separator <= 0 || separator == value.lastIndex) null
            else value.substring(0, separator) to value.substring(separator + 1)
        }.toMap().toMutableMap()

        tags.putIfAbsent("context", contextName)
        tags.putIfAbsent("error_type", "none")
        return tags
    }

    private fun extractContextFromClass(joinPoint: ProceedingJoinPoint): String {
        val className = joinPoint.target::class.simpleName?.lowercase() ?: "unknown"
        return when {
            "controller" in className -> "controller"
            "service" in className -> "service"
            "repository" in className || "repo" in className -> "repo"
            "manager" in className -> "manager"
            else -> "unknown"
        }
    }
}

/** Observe une exécution `Mono`, y compris son statut de rejeu idempotent. */
private fun <T : Any> Mono<NucleusIdempotencyResult<T>>.observeNucleusOperation(
    operation: MultiplannerOperation,
    registry: ObservationRegistry,
    correlationMode: NucleusCorrelationMode,
    idempotencyMode: NucleusIdempotencyMode,
    extraTags: Map<String, String>,
): Mono<T> {
    val logger = LoggerFactory.getLogger("NucleusOp")

    return Mono.deferContextual { context ->
        val correlationId = requireCorrelation(
            context = context,
            operation = operation,
            mode = correlationMode,
        )
        val observation = newObservation(
            operation = operation,
            registry = registry,
            idempotencyMode = idempotencyMode,
            extraTags = stableTags(context, extraTags),
        )
        val startedAt = System.nanoTime()
        val replay = AtomicBoolean(false)
        val outcome = AtomicReference("UNKNOWN")

        logger.debug(
            "NUCLEUS_START operation={} correlationId={} idempotencyMode={}",
            operation.name,
            correlationId,
            idempotencyMode.name,
        )

        this@observeNucleusOperation
            .doOnNext { result -> replay.set(result.replay) }
            .map { result -> result.value }
            .doOnSuccess {
                outcome.set("SUCCESS")
                logger.debug(
                    "NUCLEUS_SUCCESS operation={} correlationId={} replay={}",
                    operation.name,
                    correlationId,
                    replay.get(),
                )
            }
            .doOnError { error ->
                outcome.set("ERROR")
                observation.error(error)
                logger.error(
                    "NUCLEUS_ERROR operation={} correlationId={} replay={} exceptionType={}",
                    operation.name,
                    correlationId,
                    replay.get(),
                    error.javaClass.simpleName,
                )
            }
            .doOnCancel { outcome.compareAndSet("UNKNOWN", "CANCELLED") }
            .doFinally { signal ->
                if (outcome.get() == "UNKNOWN") outcome.set(signal.name)
                observation.lowCardinalityKeyValue("outcome", outcome.get())
                observation.lowCardinalityKeyValue("replay", replay.get().toString())
                observation.stop()
                val durationMs = (System.nanoTime() - startedAt) / 1_000_000
                logger.debug(
                    "NUCLEUS_END operation={} correlationId={} signal={} durationMs={} replay={}",
                    operation.name,
                    correlationId,
                    signal,
                    durationMs,
                    replay.get(),
                )
            }
    }
}

/** Extension historique conservée pour les consommateurs existants. */
fun <T : Any> Mono<T>.observedProV2(
    operation: MultiplannerOperation,
    registry: ObservationRegistry,
    extraTags: Map<String, String> = emptyMap(),
): Mono<T> = map { value -> NucleusIdempotencyResult(value, replay = false) }
    .observeNucleusOperation(
        operation = operation,
        registry = registry,
        correlationMode = NucleusCorrelationMode.NONE,
        idempotencyMode = NucleusIdempotencyMode.NONE,
        extraTags = extraTags,
    )

/** Observe un `Flux`; l'idempotence n'est pas supportée pour les flux multiples. */
fun <T : Any> Flux<T>.observedProV2(
    operation: MultiplannerOperation,
    registry: ObservationRegistry,
    correlationMode: NucleusCorrelationMode = NucleusCorrelationMode.NONE,
    extraTags: Map<String, String> = emptyMap(),
): Flux<T> {
    val logger = LoggerFactory.getLogger("NucleusOp")

    return Flux.deferContextual { context ->
        val correlationId = requireCorrelation(
            context = context,
            operation = operation,
            mode = correlationMode,
        )
        val observation = newObservation(
            operation = operation,
            registry = registry,
            idempotencyMode = NucleusIdempotencyMode.NONE,
            extraTags = stableTags(context, extraTags),
        )
        val startedAt = System.nanoTime()
        val outcome = AtomicReference("UNKNOWN")

        logger.debug(
            "NUCLEUS_START operation={} correlationId={} idempotencyMode=NONE",
            operation.name,
            correlationId,
        )

        this@observedProV2
            .doOnComplete {
                outcome.set("SUCCESS")
                logger.debug(
                    "NUCLEUS_SUCCESS operation={} correlationId={} replay=false",
                    operation.name,
                    correlationId,
                )
            }
            .doOnError { error ->
                outcome.set("ERROR")
                observation.error(error)
                logger.error(
                    "NUCLEUS_ERROR operation={} correlationId={} exceptionType={}",
                    operation.name,
                    correlationId,
                    error.javaClass.simpleName,
                )
            }
            .doOnCancel { outcome.compareAndSet("UNKNOWN", "CANCELLED") }
            .doFinally { signal ->
                if (outcome.get() == "UNKNOWN") outcome.set(signal.name)
                observation.lowCardinalityKeyValue("outcome", outcome.get())
                observation.lowCardinalityKeyValue("replay", "false")
                observation.stop()
                val durationMs = (System.nanoTime() - startedAt) / 1_000_000
                logger.debug(
                    "NUCLEUS_END operation={} correlationId={} signal={} durationMs={} replay=false",
                    operation.name,
                    correlationId,
                    signal,
                    durationMs,
                )
            }
    }
}

private fun requireCorrelation(
    context: ContextView,
    operation: MultiplannerOperation,
    mode: NucleusCorrelationMode,
): String {
    val correlationId = context.getOrEmpty<String>(NucleusContextKeys.CORRELATION_ID)
        .orElse("unknown")

    if (
        mode == NucleusCorrelationMode.PROPAGATE_OR_GENERATE &&
        correlationId == "unknown"
    ) {
        throw MissingNucleusCorrelationContextException(operation.name)
    }
    return correlationId
}

private fun newObservation(
    operation: MultiplannerOperation,
    registry: ObservationRegistry,
    idempotencyMode: NucleusIdempotencyMode,
    extraTags: Map<String, String>,
): Observation {
    val observation = Observation.createNotStarted(operation.metricName, registry)
        .contextualName(operation.spanName)
        .lowCardinalityKeyValue("module", operation.module)
        .lowCardinalityKeyValue("op", operation.action)
        .lowCardinalityKeyValue("idempotency_mode", idempotencyMode.name.lowercase())

    extraTags.forEach { (key, value) ->
        observation.lowCardinalityKeyValue(key, value)
    }
    observation.start()
    return observation
}

private fun stableTags(
    context: ContextView,
    extraTags: Map<String, String>,
): Map<String, String> {
    val path = context.getOrEmpty<String>(NucleusContextKeys.PATH)
        .orElse("unknown")
        .lowercase()
    val userId = context.getOrEmpty<String>(NucleusContextKeys.USER_ID)
        .orElse("anonymous")

    return mapOf(
        "endpoint" to path,
        "context" to (extraTags["context"] ?: "unknown"),
        "user_id" to if (userId == "anonymous") "anonymous" else "identified",
        "error_type" to (extraTags["error_type"] ?: "none"),
    ).filterKeys { key -> key in NucleusOpAspectProFixedV2.STANDARD_TAG_KEYS }
}
