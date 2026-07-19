package com.inokey.solution.dnk.nucleus.nucleus7

import com.inokey.solution.dnk.nucleus.core.NucleusFilterOrder
import com.inokey.solution.dnk.nucleus.enum.ConstantHeader
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Order(NucleusFilterOrder.SAFETY_SHIELD)
@Principles(Principle.PROTECTION, Principle.LIFE)
@PrincipleNote("Bloque les requêtes si le score de sécurité est inférieur au seuil configuré")
class SafetyShield(
    private val props: SafetyProps,
    private val scoreResolver: NucleusSafetyScoreResolver?,
    meter: MeterRegistry
) : WebFilter {

    private val blocked = meter.counter("nucleus7.safety.blocked")
    private val pm = AntPathMatcher()

    override fun filter(ex: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (!props.enabled) return chain.filter(ex)

        val path = ex.request.path.value()
        val enforceByPath = props.enforcePaths.any { pm.match(it, path) }
        if (!enforceByPath) return chain.filter(ex)

        if (scoreResolver == null) {
            blocked.increment()
            return reject(ex, "SAFETY_NO_RESOLVER")
        }

        return scoreResolver.resolve(ex)
            .defaultIfEmpty(-1.0)
            .flatMap { score: Double ->
                if (score < 0 || score < props.minScore) {
                    blocked.increment()
                    reject(ex, "SAFETY_BLOCKED")
                } else {
                    chain.filter(ex)
                }
            }
    }

    private fun reject(ex: ServerWebExchange, code: String): Mono<Void> {
        val resp = ex.response
        resp.statusCode = HttpStatus.FORBIDDEN
        resp.headers.contentType = MediaType.APPLICATION_JSON
        val correlationId = ex.request.headers.getFirst(ConstantHeader.CORRELATION_ID) ?: ""
        val message = when (code) {
            "SAFETY_NO_RESOLVER" -> "Safety resolver not configured."
            "SAFETY_BLOCKED" -> "Safety score below threshold."
            else -> "Request blocked."
        }
        val body = """{"code":"$code","message":"$message","correlationId":"$correlationId"}"""
        val buf = resp.bufferFactory().wrap(body.toByteArray())
        return resp.writeWith(Mono.just(buf))
    }
}
