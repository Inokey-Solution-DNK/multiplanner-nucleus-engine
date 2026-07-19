# NUCLEUS-PUBLIC-API.md

## Module: nucleus-core

**Coordonnées Maven**: `com.inokey.solution.dnk:nucleus-core:1.0.0-SNAPSHOT`

---

### 1. `Problem` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.problem`
- **Type**: `data class`
- **Constructeur**: `Problem(code: String, message: String, timestamp: Instant = Instant.now(), path: String = "", errorId: String = UUID.randomUUID().toString(), details: List<Map<String, Any?>> = emptyList())`
- **Propriétés**: `code`, `message`, `timestamp`, `path`, `errorId`, `details`
- **Dépendances**: `java.time.Instant`, `java.util.UUID`
- **Stabilité**: PUBLIC_STABLE — utilisé par Common via import
- **Exemple**:
```kotlin
val problem = Problem(
    code = "NOT_FOUND",
    message = "Ressource introuvable.",
    path = "/api/resource/123"
)
```

---

### 2. `NucleusHeaders` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.core`
- **Type**: `object`
- **Constantes**: `CORRELATION_ID`, `SESSION_ID`, `CONSENT_VERSION`, `IDEMPOTENCY_KEY`, `REQUEST_TIMING`
- **Stabilité**: PUBLIC_STABLE
- **Consommateur**: NucleusWebFilter, Common GlobalExceptionHandler

---

### 3. `ConstantHeader` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.enum`
- **Type**: `object`
- **Constantes**: `CONSENT_VERSION`, `LATENCY_OVERBUDGET`, `LATENCY_BUDGET`, `IDEMPOTENCY_KEY`, `SAFETY_SCORE`, `SECURITY_TOKEN`, `REQUEST_SIGNATURE`, `TRACE_ID`, `CORRELATION_ID`, `USER_CONTEXT`, `EXPERIMENT_ID`, `USER_MOOD`, `MULTIPLANNER_VERSION`, `MULTIPLANNER_MODULE`, `MULTIPLANNER_VENDOR`, `MULTIPLANNER_TIMESTAMP`
- **Stabilité**: PUBLIC_STABLE — redondant avec `NucleusHeaders` pour certaines constantes

---

### 4. `NucleusHeader` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.enum`
- **Type**: `enum class` avec `headerName: String`
- **Méthodes**: `from(name: String?): NucleusHeader?`
- **Stabilité**: PUBLIC_STABLE — redondant avec `ConstantHeader`

---

### 5. `NucleusOp` (core) — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.annotation`
- **Type**: `annotation class`
- **Cible**: `FUNCTION`
- **Paramètres**: `code: String`, `surface: String = ""`
- **Stabilité**: PUBLIC_STABLE

---

### 6. `NucleusOp` (observability) — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.observability.autoconfigure`
- **Type**: `annotation class`
- **Cible**: `FUNCTION`
- **Paramètres**: `value: MultiplannerOperation`, `extraTags: Array<String> = []`
- **Stabilité**: PUBLIC_STABLE

---

### 7. `MultiPlannerSignature` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.annotation`
- **Type**: `annotation class`
- **Cible**: `CLASS`
- **Paramètres**: `version: String = "V1"`, `module: String = "CORE"`
- **Stabilité**: PUBLIC_STABLE

---

### 8. `Principles` / `PrincipleNote` / `Principle` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- `Principles`: annotation, cible CLASS/FUNCTION, `vararg value: Principle`
- `PrincipleNote`: annotation, cible CLASS/FUNCTION, `message: String`
- `Principle`: enum — `LIFE`, `LOVE`, `INTELLECT`, `EMOTION`, `ACTION`, `IMAGINATION`, `PROTECTION`
- **Stabilité**: PUBLIC_STABLE

---

### 9. `PrincipleResolver` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `object`
- **Méthode**: `resolve(exchange: ServerWebExchange): Set<Principle>`
- **Stabilité**: PUBLIC_STABLE

---

### 10. `PrincipleRegistry` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `@Component class`
- **Constructeur**: `(applicationContext: ApplicationContext, meterRegistry: MeterRegistry)`
- **Méthodes publiques**: `rebuild(): Map<Principle, Set<String>>`, `get(): Map<Principle, Set<String>>`, `info(): Map<String, Any?>`
- **Bean**: auto-configuré via `@Component` + `@ConditionalOnClass`
- **Stabilité**: PUBLIC_STABLE

---

### 11. `ConsentGuard` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `WebFilter`
- **Constructeur**: `(props: ConsentProperties, meter: MeterRegistry)`
- **Bean**: créé par `Nucleus7Config`
- **Stabilité**: PUBLIC_STABLE

---

### 12. `ConsentGuardWebFilter` — DEPRECATED

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `@Component WebFilter`
- **Constructeur**: `(consentValidator: ConsentVersionValidator)`
- **Notes**: Doublon avec `ConsentGuard`, hardcoded pour `/api/users/register/`
- **Stabilité**: DEPRECATED — chevauchement avec `ConsentGuard`

---

### 13. `ConsentVersionValidator` — PUBLIC_STABLE (SPI)

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `interface`
- **Méthode**: `fun isAccepted(version: String): Boolean`
- **Stabilité**: PUBLIC_STABLE

---

### 14. `StaticConsentVersionValidator` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `@Component`
- **Constructeur**: `(@Value("\${multiplanner.consent.required-version}") required: String)`
- **Stabilité**: PUBLIC_STABLE — nécessite propriété obligatoire

---

### 15. `ConsentProperties` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `@ConfigurationProperties("nucleus7.consent") data class`
- **Champs**: `enabled: Boolean = true`, `headerName: String`, `requiredOnWrite: Boolean = true`, `whitelistPaths: List<String>`
- **Stabilité**: PUBLIC_STABLE

---

### 16. `SafetyShield` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `WebFilter`
- **Constructeur**: `(props: SafetyProps, meter: MeterRegistry)`
- **Bean**: créé par `Nucleus7Config`
- **Stabilité**: PUBLIC_STABLE

---

### 17. `SafetyProps` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `@ConfigurationProperties("nucleus7.safety") data class`
- **Champs**: `enabled: Boolean = true`, `minScore: Double = 0.80`, `enforcePaths: List<String> = emptyList()`
- **Stabilité**: PUBLIC_STABLE

---

### 18. `LatencyBudgetFilter` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `WebFilter`
- **Constructeur**: `(props: LatencyProps, meter: MeterRegistry)`
- **Bean**: créé par `Nucleus7Config`
- **Stabilité**: PUBLIC_STABLE

---

### 19. `LatencyProps` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `@ConfigurationProperties("nucleus7.latency") data class`
- **Champs**: `enabled: Boolean = true`, `budgetMs: Long = 250`, `hardBlock: Boolean = false`
- **Stabilité**: PUBLIC_STABLE

---

### 20. `JsonSchemaValidationFilter` — PUBLIC_EXPERIMENTAL

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `WebFilter` (stub)
- **Stabilité**: PUBLIC_EXPERIMENTAL — non implémenté (TODO)

---

### 21. `Nucleus7Config` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.nucleus7`
- **Type**: `@Configuration class`
- **Beans**: `consentGuard`, `safetyShield`, `latencyBudgetFilter`, `jsonSchemaValidationFilter`
- **Stabilité**: PUBLIC_STABLE

---

### 22. `NucleusErrorMapper` — PUBLIC_STABLE (SPI)

- **Package**: `com.inokey.solution.dnk.nucleus.spi`
- **Type**: `interface`
- **Méthode**: `fun mapError(ex: Throwable, path: String): Problem?`
- **Stabilité**: PUBLIC_STABLE

---

### 23. `NucleusObservationContributor` — PUBLIC_STABLE (SPI)

- **Package**: `com.inokey.solution.dnk.nucleus.spi`
- **Type**: `interface`
- **Méthode**: `fun contribute(context: MutableMap<String, String>, path: String, method: String, queryParams: Map<String, String> = emptyMap())`
- **Stabilité**: PUBLIC_STABLE

---

### 24. `NucleusOperationResolver` — PUBLIC_STABLE (SPI)

- **Package**: `com.inokey.solution.dnk.nucleus.spi`
- **Type**: `interface`
- **Méthode**: `fun resolve(path: String, method: String): String?`
- **Stabilité**: PUBLIC_STABLE

---

### 25. `ContractIntrospector` — PUBLIC_EXPERIMENTAL

- **Package**: `com.inokey.solution.dnk.nucleus.contract`
- **Type**: `@Component class`
- **Constructeur**: `(@Value("\${multiplanner.contract.model-package:...}") modelPackage: String)`
- **Méthodes**: `snapshot(): Map<String, Any>`
- **Data classes**: `ContractParamInfo`, `ContractClassInfo`
- **Stabilité**: PUBLIC_EXPERIMENTAL — scan par réflexion

---

### 26. `RequestMetaDom` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.domain`
- **Type**: `data class`
- **Champs**: `correlationId: UUID?`, `consentVersion: String`, `idempotencyKey: UUID?`
- **Méthodes**: `resolvedCorrelationId(): UUID`, `correlationIdString(): String`
- **Stabilité**: PUBLIC_STABLE

---

### 27. `NucleusJsonErrorHandler` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.error`
- **Type**: `@RestControllerAdvice`
- **Handlers**: `handleDecodingError(Throwable)`, `handleValidationError(WebExchangeBindException)`
- **Data class**: `ErrorResponse(code, message, details)`
- **Stabilité**: PUBLIC_STABLE

---

### 28. `MultiPlannerSignatureFilter` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.filter`
- **Type**: `@Component WebFilter`
- **Stabilité**: PUBLIC_STABLE

---

### 29. `MultiPlannerSignatureLogger` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.logging`
- **Type**: `@Component`
- **Stabilité**: PUBLIC_STABLE

---

### 30. Enums métier — PUBLIC_STABLE

- `AccountType` — `PERSON`, `ORGANIZATION` + `decode()`, `decodeOrDefault()`, `requireValid()`, `isOrganization()`
- `Audience` — `B2C`, `B2B`, `B2G`, `B2J`, `ADMIN`, `PUBLIC` + `decode()`
- `AuthProvider` — `KEYCLOAK`, `TEST`, `ANONYMOUS` + `fromString()`, `fromIssuer()`
- `OriginIdp` — `LOCAL`, `GOOGLE`, `APPLE`, `FACEBOOK`, `MICROSOFT`, `OPENAI`, `UNKNOWN` + `fromKeycloakClaim()`, `fromName()`, `isSocialBroker()`
- `PolicyEffect` — `ALLOW`, `DENY` + `fromString()`
- `MultiplannerProject` — 11 projets + `description()`, `descriptor()`, `safeDefaultNotificationTemplate()`, `safeTemplateFor()`
- `NotificationType` — `REGISTRATION`, `REMINDER`, `INVITE`
- `ProjectDescriptor` — data class

---

## Module: nucleus-observability-spring-boot-starter

**Coordonnées Maven**: `com.inokey.solution.dnk:nucleus-observability-spring-boot-starter:1.0.0-SNAPSHOT`

---

### 31. `NucleusProperties` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.observability`
- **Type**: `@ConfigurationProperties(prefix = "nucleus") data class`
- **Champs**: `enabled`, `applicationCode`, `logLevel`, `latencyBudgetMs`, `defaultSafetyScoreThreshold`, `tracingEnabled`, `metricsEnabled`, `observability: ObservabilityProperties`, `guard: GuardProperties`
- **Nested**: `ObservabilityProperties(enabled, captureRequestBody, captureResponseBody, correlationHeader, sessionHeader)`, `GuardProperties(consentCheckEnabled, safetyCheckEnabled, safetyScoreThreshold, latencyBudgetMs, metricsEnabled)`
- **Stabilité**: PUBLIC_STABLE

---

### 32. `MultiplannerOperation` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.observability.autoconfigure`
- **Type**: `enum class` (~150+ entrées)
- **Propriétés dérivées**: `metricName: String`, `spanName: String`, `otelName: String`
- **Stabilité**: PUBLIC_STABLE

---

### 33. `NucleusObservabilityAutoConfiguration` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.observability.autoconfigure`
- **Type**: `@Configuration`
- **Conditions**: `@ConditionalOnProperty("nucleus.enabled"=true, matchIfMissing=true)`, `@ConditionalOnWebApplication(REACTIVE)`
- **Beans**: `NucleusWebFilter`, `NucleusOpAspectProFixedV2`, `NucleusOpsInfoContributor`, `QuotaMetricsService`
- **Stabilité**: PUBLIC_STABLE

---

### 34. `NucleusOpAspectProFixedV2` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.observability.autoconfigure`
- **Type**: `@Aspect @Component`
- **Constructeur**: `(observationRegistry: ObservationRegistry)`
- **Extensions**: `Mono<T>.observedProV2()`, `Flux<T>.observedProV2()`
- **Stabilité**: PUBLIC_STABLE

---

### 35. `NucleusOpsInfoContributor` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.observability.autoconfigure`
- **Type**: `@Component`
- **Stabilité**: PUBLIC_STABLE

---

### 36. `QuotaMetricsService` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.observability.autoconfigure`
- **Type**: `@Component`
- **Méthodes**: `updateQuotaRemaining()`, `incrementQuotaConsumed()`, `updateProviderCost()`, `recordExternalApiCall()`, `recordMeteoCall()`, `recordTraficCall()`, `recordLlmCall()`, `updateCircuitBreakerState()`, `getQuotaSummary()`
- **Nested**: `QuotaSummary`, `ApiCallStatus`, `CircuitBreakerState`
- **Stabilité**: PUBLIC_STABLE

---

### 37. `NucleusWebFilter` — PUBLIC_STABLE

- **Package**: `com.inokey.solution.dnk.nucleus.observability.filter`
- **Type**: `WebFilter, Ordered`
- **Constructeur**: `(properties: NucleusProperties, operationResolver: NucleusOperationResolver?, contributors: List<NucleusObservationContributor>)`
- **Stabilité**: PUBLIC_STABLE

---

## Résumé de stabilité

| Statut | Count |
|--------|-------|
| PUBLIC_STABLE | 33 |
| PUBLIC_EXPERIMENTAL | 2 |
| DEPRECATED | 1 |
| INTERNAL | 1 (`ErrorResponse` dans `NucleusJsonErrorHandler`) |
| UNUSED | 0 |
| MISSING_TEST | ALL (aucun test n'existe) |
