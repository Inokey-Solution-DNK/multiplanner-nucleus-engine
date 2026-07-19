package com.inokey.solution.dnk.nucleus.observability.filter

import com.inokey.solution.dnk.nucleus.core.NucleusFilterOrder
import com.inokey.solution.dnk.nucleus.enum.ConstantHeader
import com.inokey.solution.dnk.nucleus.observability.NucleusProperties
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
 * WebFilter réactif Nucleus — intercepte chaque requête HTTP pour :
 *
 * 1. Propager / générer le X-Correlation-Id
 * 2. Injecter le correlation id dans le Reactor Context (pas MDC sur thread non-blocking)
 * 3. Résoudre le code d'opération Nucleus (SPI)
 * 4. Enrichir les tags d'observabilité (SPI)
 * 5. Mesurer le temps de traitement
 * 6. Ajouter X-Request-Timing dans la réponse
 *
 * MDC is set in doOnEach/onNext and cleared in doFinally to avoid leakage on reactive thread shifts.
 */
@Order(NucleusFilterOrder.NUCLEUS_WEB_FILTER)
class NucleusWebFilter(
    private val properties: NucleusProperties,
    private val operationResolver: NucleusOperationResolver?,
    private val contributors: List<NucleusObservationContributor>
) : WebFilter {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (!properties.observability.enabled) {
            return chain.filter(exchange)
        }

        val request = exchange.request
        val start = System.nanoTime()

        val correlationId = request.headers.getFirst(properties.observability.correlationHeader)
            ?: UUID.randomUUID().toString()

        val path = request.uri.path
        val method = request.method.name()
        val operation = operationResolver?.resolve(path, method) ?: "unknown"

        val tags = mutableMapOf(
            "app" to properties.applicationCode,
            "operation" to operation,
            "method" to method
        )
        val queryParams = request.queryParams.toSingleValueMap()
        contributors.forEach { it.contribute(tags, path, method, queryParams) }

        val mutatedRequest: ServerHttpRequest = exchange.request.mutate()
            .header(ConstantHeader.CORRELATION_ID, correlationId)
            .build()

        val mutatedExchange = exchange.mutate().request(mutatedRequest).build()

        mutatedExchange.response.beforeCommit {
            val elapsedMs = (System.nanoTime() - start) / 1_000_000
            try {
                mutatedExchange.response.headers.set(ConstantHeader.REQUEST_TIMING, "${elapsedMs}ms")
                if (mutatedExchange.response.headers[ConstantHeader.CORRELATION_ID] == null) {
                    mutatedExchange.response.headers.set(ConstantHeader.CORRELATION_ID, correlationId)
                }
            } catch (e: UnsupportedOperationException) {
                log.trace(
                    "Nucleus: headers already locked [{} {} {}ms] — {}",
                    method, path, elapsedMs, e.message
                )
            }
            Mono.empty()
        }

        return chain.filter(mutatedExchange)
            .contextWrite { ctx ->
                ctx.put("nucleus.correlationId", correlationId)
                    .put("nucleus.operation", operation)
                    .put("nucleus.path", path)
                    .put("nucleus.method", method)
            }
            .doOnEach { signal ->
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
                        "Nucleus [{}] {} {} — {}ms — tags={}",
                        operation, method, path, elapsedMs, tags
                    )
                } finally {
                    MDC.clear()
                }
            }
    }
}

