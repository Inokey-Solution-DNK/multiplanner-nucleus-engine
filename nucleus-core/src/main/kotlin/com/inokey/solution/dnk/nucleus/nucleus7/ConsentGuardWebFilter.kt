package com.inokey.solution.dnk.nucleus.nucleus7
import com.inokey.solution.dnk.nucleus.enum.ConstantHeader
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * @deprecated Use [ConsentGuard] instead. This class is kept for compatibility
 * and is not auto-registered as a bean.
 */
@Deprecated("Use ConsentGuard instead. Not auto-registered.", level = DeprecationLevel.WARNING)
class ConsentGuardWebFilter(
    private val consentValidator: ConsentVersionValidator
) : WebFilter {
    private val log = LoggerFactory.getLogger(ConsentGuardWebFilter::class.java)
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val path = request.path.pathWithinApplication().value()
        val method = request.method.name()
        val isWriteEndpoint = method == "POST" || method == "PUT" || method == "PATCH" || method == "DELETE"
        if (!isWriteEndpoint) {
            return chain.filter(exchange)
        }
        val version = request.headers.getFirst(ConstantHeader.CONSENT_VERSION)
        if (version.isNullOrBlank()) {
            log.warn("[CONSENT_GUARD_DEPRECATED] Missing {} for {} {}", ConstantHeader.CONSENT_VERSION, method, path)
            return reject(exchange, HttpStatus.BAD_REQUEST, "CONSENT_MISSING")
        }
        if (!consentValidator.isAccepted(version)) {
            log.warn("[CONSENT_GUARD_DEPRECATED] Invalid consent version for {} {}", method, path)
            return reject(exchange, HttpStatus.FORBIDDEN, "CONSENT_INVALID")
        }
        return chain.filter(exchange)
    }
    private fun reject(
        exchange: ServerWebExchange,
        status: HttpStatus,
        code: String
    ): Mono<Void> {
        val response = exchange.response
        response.statusCode = status
        response.headers.contentType = MediaType.APPLICATION_JSON
        val correlationId =
            exchange.request.headers.getFirst(ConstantHeader.CORRELATION_ID)
                ?: UUID.randomUUID().toString()
        val message = when (code) {
            "CONSENT_MISSING" -> "Consent header required for write operations."
            "CONSENT_INVALID" -> "Consent version not accepted."
            else -> "Request rejected."
        }
        val bufferFactory = response.bufferFactory()
        val bytes = """{"code":"$code","message":"$message","correlationId":"$correlationId"}"""
            .toByteArray(Charsets.UTF_8)
        val buffer = bufferFactory.wrap(bytes)
        return response.writeWith(Mono.just(buffer))
    }
}
