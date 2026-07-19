package com.inokey.solution.dnk.nucleus.domain

import java.util.UUID

/**
 * Métadonnées de requête (traçabilité + consentement + idempotence).
 *
 * ✅ Transporté du controller vers les services
 * ✅ Immuable
 * ✅ Aucune logique métier
 *
 * @param correlationId ID de traçabilité (optionnel, généré si absent)
 * @param consentVersion Version CGU acceptée
 * @param idempotencyKey Clé d'idempotence (optionnel)
 */
data class RequestMetaDom(
    val correlationId: UUID? = null,
    val consentVersion: String,
    val idempotencyKey: UUID? = null
) {
    private val resolvedId: UUID by lazy { correlationId ?: UUID.randomUUID() }

    /**
     * Retourne le correlationId, en générant un UUID stable si absent.
     * La même instance retourne toujours la même valeur.
     */
    fun resolvedCorrelationId(): UUID = resolvedId

    /**
     * Retourne le correlationId comme String.
     */
    fun correlationIdString(): String = resolvedId.toString()
}
