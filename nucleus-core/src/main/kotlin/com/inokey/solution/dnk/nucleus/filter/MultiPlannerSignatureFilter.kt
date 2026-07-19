package com.inokey.solution.dnk.nucleus.filter

import com.inokey.solution.dnk.nucleus.annotation.MultiPlannerSignature
import com.inokey.solution.dnk.nucleus.core.NucleusFilterOrder
import com.inokey.solution.dnk.nucleus.enum.ConstantHeader
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Instant

/**
 * 🌐 Filtre Web réactif qui injecte automatiquement les headers de signature MultiPlanner
 * dans chaque réponse HTTP, basé sur l'annotation @MultiPlannerSignature du contrôleur.
 *
 * Headers injectés :
 * - X-Multiplanner-Version (ex: "V1")
 * - X-Multiplanner-Module (ex: "Nucleus")
 * - X-Multiplanner-Vendor (toujours "INOKEY-SOLUTION-DNK")
 * - X-Multiplanner-Timestamp (ISO-8601)
 */
@Order(NucleusFilterOrder.SIGNATURE_FILTER)
class MultiPlannerSignatureFilter : WebFilter {

    private val log = LoggerFactory.getLogger(javaClass)
    private val vendor = "INOKEY-SOLUTION-DNK"

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        exchange.response.beforeCommit {
            val headers = exchange.response.headers
            val handlerMethod = exchange.getAttribute<Any>(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)
            try {
                if (handlerMethod is HandlerMethod) {
                    val signature = handlerMethod.beanType.getAnnotation(MultiPlannerSignature::class.java)
                    if (signature != null) {
                        headers.set(ConstantHeader.MULTIPLANNER_VERSION, signature.version)
                        headers.set(ConstantHeader.MULTIPLANNER_MODULE, signature.module)
                        headers.set(ConstantHeader.MULTIPLANNER_VENDOR, vendor)
                        headers.set(ConstantHeader.MULTIPLANNER_TIMESTAMP, Instant.now().toString())
                    }
                }
            } catch (e: UnsupportedOperationException) {
                log.trace("MultiPlannerSignatureFilter: headers déjà verrouillés — {}", e.message)
            }
            Mono.empty()
        }
        return chain.filter(exchange)
    }
}

