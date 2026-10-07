package com.inokey.solution.dnk.nucleus.observability.filter

import com.inokey.solution.dnk.nucleus.core.NucleusFilterOrder
import com.inokey.solution.dnk.nucleus.enum.ConstantHeader
import com.inokey.solution.dnk.nucleus.observability.NucleusProperties
import com.inokey.solution.dnk.nucleus.observability.context.NucleusContextKeys
import com.inokey.solution.dnk.nucleus.spi.NucleusObservationContributor
import com.inokey.solution.dnk.nucleus.spi.NucleusOperationResolver
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Filtre WebFlux transversal Nucleus.
 *
 * Il est l'unique propriétaire HTTP de `X-Correlation-Id` et de la lecture de
 * `Idempotency-Key`. Les contrôleurs n'ont pas à recevoir ces paramètres.
 */
@Order(NucleusFilterOrder.NUCLEUS_WEB_FILTER)
class NucleusWebFilter(
    private val properties: NucleusProperties,
    private val operationResolver: NucleusOperationResolver?,
    private val contributors: List<NucleusObservationContributor>,
) : WebFilter {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val start = System.nanoTime()

        val correlationId = request.headers
            .getFirst(properties.observability.correlationHeader)
            ?.trim()
            ?.takeIf(String::isNotEmpty)
            ?: UUID.randomUUID().toString()

        val idempotencyKey = request.headers
            .getFirst(ConstantHeader.IDEMPOTENCY_KEY)
            ?.trim()
            ?.takeIf(String::isNotEmpty)

        val path = request.uri.path
        val method = request.method.name()
        val operation = operationResolver?.resolve(path, method) ?: "unknown"

        val tags = mutableMapOf(
            "app" to properties.applicationCode,
            "operation" to operation,
            "method" to method,
        )
        val queryParams = request.queryParams.toSingleValueMap()
        contributors.forEach { contributor ->
            contributor.contribute(tags, path, method, queryParams)
        }

        val mutatedRequest: ServerHttpRequest = request.mutate()
            .header(ConstantHeader.CORRELATION_ID, correlationId)
            .build()
        val mutatedExchange = exchange.mutate().request(mutatedRequest).build()

        mutatedExchange.response.headers.set(
            ConstantHeader.CORRELATION_ID,
            correlationId,
        )

        mutatedExchange.response.beforeCommit {
            val elapsedMs = (System.nanoTime() - start) / 1_000_000

            try {
                if (properties.observability.enabled) {
                    mutatedExchange.response.headers.set(
                        ConstantHeader.REQUEST_TIMING,
                        "${elapsedMs}ms",
                    )
                }
            } catch (exception: UnsupportedOperationException) {
                log.trace(
                    "Nucleus response headers already committed for {} {}: {}",
                    method,
                    path,
                    exception.javaClass.simpleName,
                )
            }

            Mono.empty()
        }

        var pipeline = chain.filter(mutatedExchange)
            .contextWrite { context ->
                var enriched = context
                    .put(NucleusContextKeys.CORRELATION_ID, correlationId)
                    .put(NucleusContextKeys.OPERATION, operation)
                    .put(NucleusContextKeys.PATH, path)
                    .put(NucleusContextKeys.METHOD, method)

                if (idempotencyKey != null) {
                    enriched = enriched.put(NucleusContextKeys.IDEMPOTENCY_KEY, idempotencyKey)
                }
                enriched
            }

        if (properties.observability.enabled) {
            pipeline = pipeline
                .doOnEach {
                    MDC.put("correlationId", correlationId)
                    MDC.put("application", properties.applicationCode)
                    MDC.put("operation", operation)
                }
                .doFinally {
                    val elapsedMs = (System.nanoTime() - start) / 1_000_000
                    try {
                        MDC.put("correlationId", correlationId)
                        MDC.put("application", properties.applicationCode)
                        MDC.put("operation", operation)
                        log.debug(
                            "Nucleus operation={} method={} path={} durationMs={} tags={}",
                            operation,
                            method,
                            path,
                            elapsedMs,
                            tags,
                        )
                    } finally {
                        MDC.clear()
                    }
                }
        }

        return pipeline
    }
}
