package com.inokey.solution.dnk.nucleus.observability

import com.inokey.solution.dnk.nucleus.enum.ConstantHeader
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * Configuration publique du starter Nucleus.
 *
 * La corrélation est active par défaut. L'idempotence reste désactivée tant
 * qu'un profil consommateur ne l'active pas explicitement et ne fournit pas
 * un `NucleusIdempotencyScopeResolver`.
 */
@ConfigurationProperties(prefix = "nucleus")
data class NucleusProperties(
    val enabled: Boolean = true,
    val applicationCode: String = "unknown",
    val logLevel: String = "INFO",
    val latencyBudgetMs: Long = 5000L,
    val defaultSafetyScoreThreshold: Double = 0.80,
    val tracingEnabled: Boolean = true,
    val metricsEnabled: Boolean = true,
    val observability: ObservabilityProperties = ObservabilityProperties(),
    val idempotency: IdempotencyProperties = IdempotencyProperties(),
    val guard: GuardProperties = GuardProperties(),
) {
    data class ObservabilityProperties(
        val enabled: Boolean = true,
        val captureRequestBody: Boolean = false,
        val captureResponseBody: Boolean = false,
        val correlationHeader: String = ConstantHeader.CORRELATION_ID,
        val sessionHeader: String = ConstantHeader.SESSION_ID,
    )

    data class IdempotencyProperties(
        val enabled: Boolean = false,
        val ttl: Duration = Duration.ofHours(24),
        val maxEntries: Int = 10_000,
        val maxKeyLength: Int = 160,
    )

    data class GuardProperties(
        val consentCheckEnabled: Boolean = true,
        val safetyCheckEnabled: Boolean = false,
        val safetyScoreThreshold: Double = 0.80,
        val latencyBudgetMs: Long = 5000L,
        val metricsEnabled: Boolean = true,
    )
}
