package com.inokey.solution.dnk.nucleus.observability.autoconfigure

import com.inokey.solution.dnk.nucleus.observability.NucleusProperties
import com.inokey.solution.dnk.nucleus.observability.filter.NucleusWebFilter
import com.inokey.solution.dnk.nucleus.observability.idempotency.NucleusIdempotencyCoordinator
import com.inokey.solution.dnk.nucleus.observability.idempotency.NucleusIdempotencyExecutor
import com.inokey.solution.dnk.nucleus.observability.idempotency.NucleusIdempotencyFingerprintFactory
import com.inokey.solution.dnk.nucleus.observability.idempotency.NucleusIdempotencyScopeResolver
import com.inokey.solution.dnk.nucleus.spi.NucleusObservationContributor
import com.inokey.solution.dnk.nucleus.spi.NucleusOperationResolver
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.ObservationRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

/**
 * Auto-configuration canonique du starter Nucleus observability.
 *
 * Active les composants suivants si `nucleus.enabled` vaut `true` (par défaut) :
 * - [NucleusProperties]
 * - [NucleusWebFilter]
 * - [NucleusOpAspectProFixedV2]
 * - [NucleusIdempotencyExecutor] (uniquement si un [NucleusIdempotencyCoordinator]
 *   est fourni par l'application)
 * - [QuotaMetricsService]
 * - [NucleusOpsInfoContributor]
 */
@Configuration
@EnableConfigurationProperties(NucleusProperties::class)
@ConditionalOnProperty(
    prefix = "nucleus",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
@EnableAspectJAutoProxy
class NucleusObservabilityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun nucleusIdempotencyFingerprintFactory(): NucleusIdempotencyFingerprintFactory =
        NucleusIdempotencyFingerprintFactory()

    @Bean
    @ConditionalOnBean(NucleusIdempotencyCoordinator::class)
    fun nucleusIdempotencyExecutor(
        properties: NucleusProperties,
        fingerprintFactory: NucleusIdempotencyFingerprintFactory,
        coordinator: NucleusIdempotencyCoordinator,
        scopeResolver: NucleusIdempotencyScopeResolver?,
    ): NucleusIdempotencyExecutor =
        NucleusIdempotencyExecutor(
            properties = properties,
            fingerprintFactory = fingerprintFactory,
            coordinator = coordinator,
            scopeResolver = scopeResolver,
        )

    @Bean
    @ConditionalOnClass(ObservationRegistry::class)
    @ConditionalOnMissingBean
    fun nucleusOpAspectProFixedV2(
        observationRegistry: ObservationRegistry,
        idempotencyExecutor: NucleusIdempotencyExecutor?,
    ): NucleusOpAspectProFixedV2 =
        NucleusOpAspectProFixedV2(
            observationRegistry = observationRegistry,
            idempotencyExecutor = idempotencyExecutor,
        )

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @ConditionalOnMissingBean
    fun nucleusWebFilter(
        properties: NucleusProperties,
        operationResolver: NucleusOperationResolver?,
        contributors: List<NucleusObservationContributor>,
    ): NucleusWebFilter =
        NucleusWebFilter(
            properties = properties,
            operationResolver = operationResolver,
            contributors = contributors,
        )

    @Bean
    @ConditionalOnClass(MeterRegistry::class)
    @ConditionalOnMissingBean
    fun quotaMetricsService(meterRegistry: MeterRegistry): QuotaMetricsService =
        QuotaMetricsService(meterRegistry)

    @Bean
    @ConditionalOnClass(name = ["org.springframework.boot.actuate.info.InfoContributor"])
    @ConditionalOnMissingBean
    fun nucleusOpsInfoContributor(): NucleusOpsInfoContributor =
        NucleusOpsInfoContributor()
}
