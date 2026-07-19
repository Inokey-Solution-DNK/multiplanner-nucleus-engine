# NUCLEUS-CONFIGURATION-REFERENCE.md

## Auto-configurations

### NucleusObservabilityAutoConfiguration

- **Fichier**: `nucleus-observability-spring-boot-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- **Classe**: `com.inokey.solution.dnk.nucleus.observability.autoconfigure.NucleusObservabilityAutoConfiguration`
- **Conditions**:
  - `@ConditionalOnProperty(prefix = "nucleus", name = ["enabled"], havingValue = "true", matchIfMissing = true)`
  - `@ConditionalOnWebApplication(type = REACTIVE)`
- **Beans créés**:

| Bean | Condition | Dépendances |
|------|-----------|-------------|
| `NucleusWebFilter` | `@ConditionalOnMissingBean` | `NucleusProperties`, `NucleusOperationResolver?`, `List<NucleusObservationContributor>` |
| `NucleusOpAspectProFixedV2` | `@ConditionalOnMissingBean` + `@ConditionalOnClass(ObservationRegistry)` | `ObservationRegistry` |
| `NucleusOpsInfoContributor` | `@ConditionalOnMissingBean` + `@ConditionalOnClass(InfoContributor)` | — |
| `QuotaMetricsService` | `@ConditionalOnMissingBean` + `@ConditionalOnBean(MeterRegistry)` | `MeterRegistry` |

### Nucleus7Config

- **Fichier**: `nucleus-core/.../nucleus7/Nucleus7Config.kt`
- **Classe**: `com.inokey.solution.dnk.nucleus.nucleus7.Nucleus7Config`
- **Conditions**: aucune (activée si `MeterRegistry` présent au runtime)
- **Beans créés**:

| Bean | Dépendances |
|------|-------------|
| `ConsentGuard` | `ConsentProperties`, `MeterRegistry` |
| `SafetyShield` | `SafetyProps`, `MeterRegistry` |
| `LatencyBudgetFilter` | `LatencyProps`, `MeterRegistry` |
| `JsonSchemaValidationFilter` | — |

### Components auto-détectés (@Component)

| Composant | Module | Condition |
|-----------|--------|-----------|
| `ConsentGuardWebFilter` | core | `@Component` — nécessite `ConsentVersionValidator` bean |
| `StaticConsentVersionValidator` | core | `@Component` — nécessite `multiplanner.consent.required-version` |
| `MultiPlannerSignatureFilter` | core | `@Component` |
| `MultiPlannerSignatureLogger` | core | `@Component` |
| `PrincipleRegistry` | core | `@Component` + `@ConditionalOnClass(RequestMappingHandlerMapping)` |
| `NucleusJsonErrorHandler` | core | `@RestControllerAdvice` |

---

## Propriétés de configuration

### `nucleus.*` (NucleusProperties)

| Property | Type | Default | Required | Env Var | Production Safety | Consumer Example |
|----------|------|---------|----------|---------|-------------------|------------------|
| `nucleus.enabled` | Boolean | true | no | NUCLEUS_ENABLED | ✅ safe | `nucleus.enabled: true` |
| `nucleus.application-code` | String | "unknown" | recommended | NUCLEUS_APPLICATION_CODE | ✅ safe | `nucleus.application-code: pertinence-engine` |
| `nucleus.log-level` | String | "INFO" | no | NUCLEUS_LOG_LEVEL | ✅ safe | `nucleus.log-level: DEBUG` |
| `nucleus.latency-budget-ms` | Long | 5000 | no | NUCLEUS_LATENCY_BUDGET_MS | ✅ safe | `nucleus.latency-budget-ms: 3000` |
| `nucleus.default-safety-score-threshold` | Double | 0.80 | no | NUCLEUS_SAFETY_SCORE_THRESHOLD | ✅ safe | `nucleus.default-safety-score-threshold: 0.90` |
| `nucleus.tracing-enabled` | Boolean | true | no | NUCLEUS_TRACING_ENABLED | ✅ safe | `nucleus.tracing-enabled: true` |
| `nucleus.metrics-enabled` | Boolean | true | no | NUCLEUS_METRICS_ENABLED | ✅ safe | `nucleus.metrics-enabled: true` |

### `nucleus.observability.*` (ObservabilityProperties)

| Property | Type | Default | Required | Env Var | Production Safety | Consumer Example |
|----------|------|---------|----------|---------|-------------------|------------------|
| `nucleus.observability.enabled` | Boolean | true | no | NUCLEUS_OBS_ENABLED | ✅ safe | `nucleus.observability.enabled: true` |
| `nucleus.observability.capture-request-body` | Boolean | false | no | NUCLEUS_OBS_CAPTURE_REQ | ⚠️ PII risk if true | `nucleus.observability.capture-request-body: false` |
| `nucleus.observability.capture-response-body` | Boolean | false | no | NUCLEUS_OBS_CAPTURE_RES | ⚠️ PII risk if true | `nucleus.observability.capture-response-body: false` |
| `nucleus.observability.correlation-header` | String | "X-Correlation-Id" | no | NUCLEUS_OBS_CORR_HEADER | ✅ safe | `nucleus.observability.correlation-header: X-Correlation-Id` |
| `nucleus.observability.session-header` | String | "X-Session-Id" | no | NUCLEUS_OBS_SESSION_HEADER | ✅ safe | `nucleus.observability.session-header: X-Session-Id` |

### `nucleus.guard.*` (GuardProperties)

| Property | Type | Default | Required | Env Var | Production Safety | Consumer Example |
|----------|------|---------|----------|---------|-------------------|------------------|
| `nucleus.guard.consent-check-enabled` | Boolean | true | no | NUCLEUS_GUARD_CONSENT | ✅ safe | `nucleus.guard.consent-check-enabled: true` |
| `nucleus.guard.safety-check-enabled` | Boolean | false | no | NUCLEUS_GUARD_SAFETY | ✅ safe | `nucleus.guard.safety-check-enabled: false` |
| `nucleus.guard.safety-score-threshold` | Double | 0.80 | no | NUCLEUS_GUARD_SAFETY_THRESHOLD | ✅ safe | `nucleus.guard.safety-score-threshold: 0.90` |
| `nucleus.guard.latency-budget-ms` | Long | 5000 | no | NUCLEUS_GUARD_LATENCY_MS | ✅ safe | `nucleus.guard.latency-budget-ms: 3000` |
| `nucleus.guard.metrics-enabled` | Boolean | true | no | NUCLEUS_GUARD_METRICS | ✅ safe | `nucleus.guard.metrics-enabled: true` |

### `nucleus7.consent.*` (ConsentProperties)

| Property | Type | Default | Required | Env Var | Production Safety | Consumer Example |
|----------|------|---------|----------|---------|-------------------|------------------|
| `nucleus7.consent.enabled` | Boolean | true | no | NUCLEUS7_CONSENT_ENABLED | ✅ safe | `nucleus7.consent.enabled: true` |
| `nucleus7.consent.header-name` | String | "X-Consent-Version" | no | NUCLEUS7_CONSENT_HEADER | ✅ safe | `nucleus7.consent.header-name: X-Consent-Version` |
| `nucleus7.consent.required-on-write` | Boolean | true | no | NUCLEUS7_CONSENT_REQUIRED | ✅ safe | `nucleus7.consent.required-on-write: true` |
| `nucleus7.consent.whitelist-paths` | List<String> | ["/actuator/**", "/auth/**"] | no | NUCLEUS7_CONSENT_WHITELIST | ✅ safe | `nucleus7.consent.whitelist-paths: ["/actuator/**", "/auth/**"]` |

### `nucleus7.safety.*` (SafetyProps)

| Property | Type | Default | Required | Env Var | Production Safety | Consumer Example |
|----------|------|---------|----------|---------|-------------------|------------------|
| `nucleus7.safety.enabled` | Boolean | true | no | NUCLEUS7_SAFETY_ENABLED | ✅ safe | `nucleus7.safety.enabled: true` |
| `nucleus7.safety.min-score` | Double | 0.80 | no | NUCLEUS7_SAFETY_MIN_SCORE | ✅ safe | `nucleus7.safety.min-score: 0.85` |
| `nucleus7.safety.enforce-paths` | List<String> | emptyList() | no | NUCLEUS7_SAFETY_ENFORCE_PATHS | ✅ safe | `nucleus7.safety.enforce-paths: ["/api/sensitive/**"]` |

### `nucleus7.latency.*` (LatencyProps)

| Property | Type | Default | Required | Env Var | Production Safety | Consumer Example |
|----------|------|---------|----------|---------|-------------------|------------------|
| `nucleus7.latency.enabled` | Boolean | true | no | NUCLEUS7_LATENCY_ENABLED | ✅ safe | `nucleus7.latency.enabled: true` |
| `nucleus7.latency.budget-ms` | Long | 250 | no | NUCLEUS7_LATENCY_BUDGET_MS | ✅ safe | `nucleus7.latency.budget-ms: 500` |
| `nucleus7.latency.hard-block` | Boolean | false | no | NUCLEUS7_LATENCY_HARD_BLOCK | ✅ safe | `nucleus7.latency.hard-block: false` |

### `multiplanner.consent.required-version` (StaticConsentVersionValidator)

| Property | Type | Default | Required | Env Var | Production Safety | Consumer Example |
|----------|------|---------|----------|---------|-------------------|------------------|
| `multiplanner.consent.required-version` | String | **NONE** | **YES** | MULTIPLANNER_CONSENT_REQUIRED_VERSION | ✅ safe | `multiplanner.consent.required-version: v1.0` |

⚠️ **Pas de valeur par défaut** — l'application échoue au démarrage si cette propriété n'est pas définie et que `StaticConsentVersionValidator` est instancié.

### `multiplanner.contract.model-package` (ContractIntrospector)

| Property | Type | Default | Required | Env Var | Production Safety | Consumer Example |
|----------|------|---------|----------|---------|-------------------|------------------|
| `multiplanner.contract.model-package` | String | `com.inokey.solution.dnk.multiplanner.contract.model` | no | MULTIPLANNER_CONTRACT_MODEL_PACKAGE | ✅ safe | `multiplanner.contract.model-package: com.inokey.solution.dnk.multiplanner.contract.model` |

---

## Vérification de sécurité

| Critère | Statut |
|---------|--------|
| Aucun secret dans le code | ✅ PASS |
| Aucun mot de passe par défaut | ✅ PASS |
| Aucune clé API par défaut | ✅ PASS |
| Propriétés sensibles (capture-request-body) | ⚠️ Default false — safe |
| Propriété obligatoire sans défaut | ⚠️ `multiplanner.consent.required-version` — doit être documentée |

## Exemple application.yml complet

```yaml
nucleus:
  enabled: true
  application-code: my-service
  log-level: INFO
  latency-budget-ms: 5000
  default-safety-score-threshold: 0.80
  tracing-enabled: true
  metrics-enabled: true
  observability:
    enabled: true
    capture-request-body: false
    capture-response-body: false
    correlation-header: X-Correlation-Id
    session-header: X-Session-Id
  guard:
    consent-check-enabled: true
    safety-check-enabled: false
    safety-score-threshold: 0.80
    latency-budget-ms: 5000
    metrics-enabled: true

nucleus7:
  consent:
    enabled: true
    header-name: X-Consent-Version
    required-on-write: true
    whitelist-paths:
      - /actuator/**
      - /auth/**
  safety:
    enabled: true
    min-score: 0.80
    enforce-paths: []
  latency:
    enabled: true
    budget-ms: 250
    hard-block: false

multiplanner:
  consent:
    required-version: v1.0
  contract:
    model-package: com.inokey.solution.dnk.multiplanner.contract.model
```
