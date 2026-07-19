package com.inokey.solution.dnk.nucleus.nucleus7

import com.inokey.solution.dnk.nucleus.core.NucleusFilterOrder
import com.inokey.solution.dnk.nucleus.enum.ConstantHeader
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Order(NucleusFilterOrder.CONSENT_GUARD)
@Principles(Principle.LIFE, Principle.PROTECTION)
@PrincipleNote("Bloque les écritures sans consent explicite (X-Consent-Version)")
class ConsentGuard(
    private val props: ConsentProperties,
    private val consentValidator: ConsentVersionValidator?,
    meter: MeterRegistry
) : WebFilter {

    private val pm = AntPathMatcher()
    private val missing = meter.counter("nucleus7.consent.missing")
    private val accepted = meter.counter("nucleus7.consent.accepted")
    private val rejected = meter.counter("nucleus7.consent.rejected")

    override fun filter(ex: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (!props.enabled) return chain.filter(ex)

        val path = ex.request.path.value()
        if (props.whitelistPaths.any { pm.match(it, path) }) return chain.filter(ex)

        val method = ex.request.method
        val isWrite = method == HttpMethod.POST || method == HttpMethod.PUT ||
                      method == HttpMethod.PATCH || method == HttpMethod.DELETE

        if (!props.requiredOnWrite || !isWrite) return chain.filter(ex)

        val header = ex.request.headers.getFirst(props.headerName)
        if (header.isNullOrBlank()) {
            missing.increment()
            return reject(ex, "CONSENT_MISSING")
        }

        if (consentValidator != null && !consentValidator.isAccepted(header)) {
            rejected.increment()
            return reject(ex, "CONSENT_INVALID")
        }

        accepted.increment()
        return chain.filter(ex)
    }

    private fun reject(ex: ServerWebExchange, code: String): Mono<Void> {
        val resp = ex.response
        resp.statusCode = HttpStatus.BAD_REQUEST
        resp.headers.contentType = MediaType.APPLICATION_JSON
        val correlationId = ex.request.headers.getFirst(ConstantHeader.CORRELATION_ID) ?: ""
        val message = when (code) {
            "CONSENT_MISSING" -> "Consent header required for write operations."
            "CONSENT_INVALID" -> "Consent version not accepted."
            else -> "Request rejected."
        }
        val body = """{"code":"$code","message":"$message","correlationId":"$correlationId"}"""
        val buf = resp.bufferFactory().wrap(body.toByteArray())
        return resp.writeWith(Mono.just(buf))
    }
}
