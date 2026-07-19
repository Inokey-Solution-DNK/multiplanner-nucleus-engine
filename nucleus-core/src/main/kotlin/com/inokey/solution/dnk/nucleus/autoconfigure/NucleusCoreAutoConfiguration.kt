package com.inokey.solution.dnk.nucleus.autoconfigure

import com.inokey.solution.dnk.nucleus.contract.ContractIntrospector
import com.inokey.solution.dnk.nucleus.filter.MultiPlannerSignatureFilter
import com.inokey.solution.dnk.nucleus.logging.MultiPlannerSignatureLogger
import com.inokey.solution.dnk.nucleus.nucleus7.ConsentGuard
import com.inokey.solution.dnk.nucleus.nucleus7.ConsentProperties
import com.inokey.solution.dnk.nucleus.nucleus7.ConsentVersionValidator
import com.inokey.solution.dnk.nucleus.nucleus7.JsonSchemaProps
import com.inokey.solution.dnk.nucleus.nucleus7.JsonSchemaValidationFilter
import com.inokey.solution.dnk.nucleus.nucleus7.LatencyBudgetFilter
import com.inokey.solution.dnk.nucleus.nucleus7.LatencyProps
import com.inokey.solution.dnk.nucleus.nucleus7.NucleusSafetyScoreResolver
import com.inokey.solution.dnk.nucleus.nucleus7.PrincipleRegistry
import com.inokey.solution.dnk.nucleus.nucleus7.SafetyProps
import com.inokey.solution.dnk.nucleus.nucleus7.SafetyShield
import com.inokey.solution.dnk.nucleus.nucleus7.StaticConsentVersionValidator
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping

/**
 * Auto-configuration Spring Boot pour Nucleus Core (nucleus7 filters + signature).
 *
 * Active conditionnellement :
 *   - ConsentGuard (si nucleus7.consent.enabled=true)
 *   - SafetyShield (si nucleus7.safety.enabled=true)
 *   - LatencyBudgetFilter (si nucleus7.latency.enabled=true)
 *   - JsonSchemaValidationFilter (si nucleus7.json-schema.enabled=true, experimental)
 *   - MultiPlannerSignatureFilter (reactive web app)
 *   - PrincipleRegistry (si RequestMappingHandlerMapping present)
 *   - ContractIntrospector (always)
 *   - StaticConsentVersionValidator (si multiplanner.consent.required-version set)
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(
    ConsentProperties::class,
    SafetyProps::class,
    LatencyProps::class,
    JsonSchemaProps::class
)
class NucleusCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(MeterRegistry::class)
    @ConditionalOnProperty(prefix = "nucleus7.consent", name = ["enabled"], havingValue = "true")
    fun consentGuard(
        props: ConsentProperties,
        consentValidator: ConsentVersionValidator?,
        meter: MeterRegistry
    ): ConsentGuard = ConsentGuard(props, consentValidator, meter)

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(MeterRegistry::class)
    @ConditionalOnProperty(prefix = "nucleus7.safety", name = ["enabled"], havingValue = "true")
    fun safetyShield(
        props: SafetyProps,
        scoreResolver: NucleusSafetyScoreResolver?,
        meter: MeterRegistry
    ): SafetyShield = SafetyShield(props, scoreResolver, meter)

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(MeterRegistry::class)
    @ConditionalOnProperty(prefix = "nucleus7.latency", name = ["enabled"], havingValue = "true")
    fun latencyBudgetFilter(props: LatencyProps, meter: MeterRegistry): LatencyBudgetFilter =
        LatencyBudgetFilter(props, meter)

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "nucleus7.json-schema", name = ["enabled"], havingValue = "true")
    fun jsonSchemaValidationFilter(props: JsonSchemaProps): JsonSchemaValidationFilter =
        JsonSchemaValidationFilter(props)

    @Bean
    @ConditionalOnMissingBean
    fun multiPlannerSignatureFilter(): MultiPlannerSignatureFilter = MultiPlannerSignatureFilter()

    @Bean
    @ConditionalOnMissingBean
    fun multiPlannerSignatureLogger(ctx: ApplicationContext): MultiPlannerSignatureLogger =
        MultiPlannerSignatureLogger(ctx)

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(RequestMappingHandlerMapping::class)
    @ConditionalOnBean(MeterRegistry::class)
    fun principleRegistry(
        ctx: ApplicationContext,
        meter: MeterRegistry,
        @Value("\${nucleus7.principle.package-prefix:com.inokey.solution.dnk}") packagePrefix: String,
        @Value("\${nucleus7.principle.ttl-seconds:60}") ttlSeconds: Long
    ): PrincipleRegistry = PrincipleRegistry(ctx, meter, packagePrefix, ttlSeconds)

    @Bean
    @ConditionalOnMissingBean
    fun contractIntrospector(
        @org.springframework.beans.factory.annotation.Value("\${multiplanner.contract.model-package:com.inokey.solution.dnk.multiplanner.contract.model}") modelPackage: String
    ): ContractIntrospector = ContractIntrospector(modelPackage)

    @Bean
    @ConditionalOnMissingBean(ConsentVersionValidator::class)
    @ConditionalOnProperty(prefix = "multiplanner.consent", name = ["required-version"])
    fun staticConsentVersionValidator(
        @org.springframework.beans.factory.annotation.Value("\${multiplanner.consent.required-version}") required: String
    ): StaticConsentVersionValidator = StaticConsentVersionValidator(required)
}
