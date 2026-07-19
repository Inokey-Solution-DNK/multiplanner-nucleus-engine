package com.inokey.solution.dnk.nucleus.nucleus7

import com.inokey.solution.dnk.nucleus.core.NucleusFilterOrder
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * EXPERIMENTAL — Stub de filtre INTELLECT : applique une validation schéma JSON
 * sur les endpoints marqués INTELLECT.
 *
 * Ce filtre est désactivé par défaut. Aucune validation réelle n'est exécutée.
 * Pour l'activer en développement, définir `nucleus7.json-schema.enabled=true`.
 */
@Order(NucleusFilterOrder.LATENCY_BUDGET_FILTER + 5)
@Principles(Principle.INTELLECT)
@PrincipleNote("Valide les payloads JSON selon un schéma si INTELLECT est présent (EXPERIMENTAL)")
class JsonSchemaValidationFilter(
    private val props: JsonSchemaProps = JsonSchemaProps()
) : WebFilter {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        if (props.enabled) {
            log.warn("[JSON_SCHEMA] EXPERIMENTAL filter enabled — no real validation is executed.")
        }
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (!props.enabled) return chain.filter(exchange)
        val principles = PrincipleResolver.resolve(exchange)
        if (Principle.INTELLECT !in principles) return chain.filter(exchange)
        // TODO: intégrer validation réelle ici
        return chain.filter(exchange)
    }
}

