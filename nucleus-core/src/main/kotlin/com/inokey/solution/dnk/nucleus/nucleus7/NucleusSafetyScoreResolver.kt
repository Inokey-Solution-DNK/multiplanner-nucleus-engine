package com.inokey.solution.dnk.nucleus.nucleus7

import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * SPI — Résout le score de sécurité d'une requête depuis une source serveur fiable.
 *
 * Le header client [com.inokey.solution.dnk.nucleus.enum.ConstantHeader.SAFETY_SCORE]
 * ne doit jamais être utilisé en production. Une implémentation de cette SPI
 * doit récupérer le score depuis le contexte d'authentification, un attribut
 * d'exchange posé par un composant approuvé, ou un moteur de sécurité.
 */
interface NucleusSafetyScoreResolver {
    /**
     * @return the safety score [0.0, 1.0], or empty Mono if not available
     */
    fun resolve(exchange: ServerWebExchange): Mono<Double>
}
