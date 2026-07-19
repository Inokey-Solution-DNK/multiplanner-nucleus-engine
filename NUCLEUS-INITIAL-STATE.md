# NUCLEUS-INITIAL-STATE.md

## Projet

- **Nom** : multiplanner-nucleus-engine
- **Root git** : `C:/multiplanner/multiplanner-back-end/multiplanner-nucleus-engine`
- **Statut git** : clean (aucun fichier non suivi ou modifié)
- **Group Maven** : `com.inokey.solution.dnk`
- **Version** : `1.0.0-SNAPSHOT`

## Structure

```
multiplanner-nucleus-engine/
├── .gitignore
├── NUCLEUS_ARCHITECTURE_COUCHES.md
├── NUCLEUS_BONNES_PRATIQUES.md
├── NUCLEUS_GUIDE_COMPLET.md
├── NUCLEUS_INDEX.md
├── README.md
├── build.gradle                          (root — plugins, subprojects config)
├── settings.gradle                       (2 modules: nucleus-core, nucleus-observability-spring-boot-starter)
├── gradle/wrapper/gradle-wrapper.jar     (jar seul — PAS de gradlew.bat ni gradle-wrapper.properties)
├── nucleus-core/
│   ├── build.gradle
│   └── src/main/kotlin/com/inokey/solution/dnk/nucleus/
│       ├── annotation/
│       ├── contract/
│       ├── core/
│       ├── domain/
│       ├── enum/
│       ├── error/
│       ├── filter/
│       ├── logging/
│       ├── nucleus7/
│       ├── problem/
│       └── spi/
├── nucleus-observability-spring-boot-starter/
│   ├── build.gradle
│   └── src/main/
│       ├── kotlin/com/inokey/solution/dnk/nucleus/observability/
│       │   ├── NucleusProperties.kt
│       │   ├── autoconfigure/
│       │   └── filter/
│       └── resources/META-INF/spring/
│           └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
└── nucleus-frontend-sdk/                 (SDK TypeScript Angular/RN — hors scope backend)
```

## Inventaire physique

### Modules Gradle (2)

| Module | Type | Description |
|--------|------|-------------|
| `nucleus-core` | `java-library` | Noyau Nucleus : headers, Problem, annotations, enums, nucleus7 guards, SPI |
| `nucleus-observability-spring-boot-starter` | `java-library` | Auto-configuration Spring Boot : WebFilter, AOP, métriques, InfoContributor |

### Fichiers Kotlin — nucleus-core (27 fichiers)

| # | Fichier | Package | Type | Symbole public |
|---|---------|---------|------|----------------|
| 1 | `annotation/MultiPlannerSignature.kt` | `nucleus.annotation` | annotation | `MultiPlannerSignature` |
| 2 | `annotation/NucleusOp.kt` | `nucleus.annotation` | annotation | `NucleusOp` |
| 3 | `contract/ContractIntrospector.kt` | `nucleus.contract` | data class + class | `ContractParamInfo`, `ContractClassInfo`, `ContractIntrospector` |
| 4 | `core/NucleusHeaders.kt` | `nucleus.core` | object | `NucleusHeaders` |
| 5 | `domain/RequestMetaDom.kt` | `nucleus.domain` | data class | `RequestMetaDom` |
| 6 | `enum/AccountType.kt` | `nucleus.enum` | enum | `AccountType` |
| 7 | `enum/Audience.kt` | `nucleus.enum` | enum | `Audience` |
| 8 | `enum/AuthProvider.kt` | `nucleus.enum` | enum | `AuthProvider` |
| 9 | `enum/ConstantHeader.kt` | `nucleus.enum` | object | `ConstantHeader` |
| 10 | `enum/MultiplannerProject.kt` | `nucleus.enum` | enum + data class + ext fun | `MultiplannerProject`, `ProjectDescriptor`, `NotificationType`, `description()`, `descriptor()`, `safeDefaultNotificationTemplate()`, `safeTemplateFor()` |
| 11 | `enum/NucleusHeader.kt` | `nucleus.enum` | enum | `NucleusHeader` |
| 12 | `enum/OriginIdp.kt` | `nucleus.enum` | enum | `OriginIdp` |
| 13 | `enum/PolicyEffect.kt` | `nucleus.enum` | enum | `PolicyEffect` |
| 14 | `error/NucleusJsonErrorHandler.kt` | `nucleus.error` | @RestControllerAdvice + data class | `NucleusJsonErrorHandler`, `ErrorResponse` |
| 15 | `filter/MultiPlannerSignatureFilter.kt` | `nucleus.filter` | @Component WebFilter | `MultiPlannerSignatureFilter` |
| 16 | `logging/MultiPlannerSignatureLogger.kt` | `nucleus.logging` | @Component | `MultiPlannerSignatureLogger` |
| 17 | `nucleus7/ConsentGuard.kt` | `nucleus.nucleus7` | WebFilter | `ConsentGuard` |
| 18 | `nucleus7/ConsentGuardWebFilter.kt` | `nucleus.nucleus7` | @Component WebFilter | `ConsentGuardWebFilter` |
| 19 | `nucleus7/ConsentProperties.kt` | `nucleus.nucleus7` | @ConfigurationProperties | `ConsentProperties` |
| 20 | `nucleus7/ConsentVersionValidator.kt` | `nucleus.nucleus7` | interface | `ConsentVersionValidator` |
| 21 | `nucleus7/JsonSchemaValidationFilter.kt` | `nucleus.nucleus7` | WebFilter (stub) | `JsonSchemaValidationFilter` |
| 22 | `nucleus7/LatencyBudgetFilter.kt` | `nucleus.nucleus7` | WebFilter | `LatencyBudgetFilter` |
| 23 | `nucleus7/LatencyProps.kt` | `nucleus.nucleus7` | @ConfigurationProperties | `LatencyProps` |
| 24 | `nucleus7/Nucleus7Config.kt` | `nucleus.nucleus7` | @Configuration | `Nucleus7Config` |
| 25 | `nucleus7/Principle.kt` | `nucleus.nucleus7` | enum | `Principle` |
| 26 | `nucleus7/PrincipleNote.kt` | `nucleus.nucleus7` | annotation | `PrincipleNote` |
| 27 | `nucleus7/PrincipleRegistry.kt` | `nucleus.nucleus7` | @Component | `PrincipleRegistry` |
| 28 | `nucleus7/PrincipleResolver.kt` | `nucleus.nucleus7` | object | `PrincipleResolver` |
| 29 | `nucleus7/Principles.kt` | `nucleus.nucleus7` | annotation | `Principles` |
| 30 | `nucleus7/SafetyProps.kt` | `nucleus.nucleus7` | @ConfigurationProperties | `SafetyProps` |
| 31 | `nucleus7/SafetyShield.kt` | `nucleus.nucleus7` | WebFilter | `SafetyShield` |
| 32 | `nucleus7/StaticConsentVersionValidator.kt` | `nucleus.nucleus7` | @Component | `StaticConsentVersionValidator` |
| 33 | `problem/Problem.kt` | `nucleus.problem` | data class | `Problem` |
| 34 | `spi/NucleusErrorMapper.kt` | `nucleus.spi` | interface | `NucleusErrorMapper` |
| 35 | `spi/NucleusObservationContributor.kt` | `nucleus.spi` | interface | `NucleusObservationContributor` |
| 36 | `spi/NucleusOperationResolver.kt` | `nucleus.spi` | interface | `NucleusOperationResolver` |

### Fichiers Kotlin — nucleus-observability-spring-boot-starter (8 fichiers)

| # | Fichier | Package | Type | Symbole public |
|---|---------|---------|------|----------------|
| 1 | `NucleusProperties.kt` | `nucleus.observability` | @ConfigurationProperties | `NucleusProperties`, `ObservabilityProperties`, `GuardProperties` |
| 2 | `autoconfigure/MultiplannerOperation.kt` | `nucleus.observability.autoconfigure` | enum | `MultiplannerOperation` (741 lignes, ~150+ opérations) |
| 3 | `autoconfigure/NucleusObservabilityAutoConfiguration.kt` | `nucleus.observability.autoconfigure` | @Configuration | `NucleusObservabilityAutoConfiguration` |
| 4 | `autoconfigure/NucleusOp.kt` | `nucleus.observability.autoconfigure` | annotation | `NucleusOp` (observability variant) |
| 5 | `autoconfigure/NucleusOpAspectProFixedV2.kt` | `nucleus.observability.autoconfigure` | @Aspect @Component + ext fun | `NucleusOpAspectProFixedV2`, `observedProV2()` |
| 6 | `autoconfigure/NucleusOpsInfoContributor.kt` | `nucleus.observability.autoconfigure` | @Component | `NucleusOpsInfoContributor` |
| 7 | `autoconfigure/QuotaMetricsService.kt` | `nucleus.observability.autoconfigure` | @Component | `QuotaMetricsService`, `QuotaSummary`, `ApiCallStatus`, `CircuitBreakerState` |
| 8 | `filter/NucleusWebFilter.kt` | `nucleus.observability.filter` | WebFilter | `NucleusWebFilter` |

### Fichiers de ressources

| Fichier | Emplacement | Rôle |
|---------|-------------|------|
| `AutoConfiguration.imports` | `nucleus-observability/.../META-INF/spring/` | Déclare `NucleusObservabilityAutoConfiguration` pour auto-config Spring Boot |

### Fichiers non source (cleanup artifacts)

| Fichier | Type | Statut |
|---------|------|--------|
| `.cleanup` | TODO list | Référence historique — à supprimer |
| `CLEANUP_LOG.md` | log de cleanup | Référence historique — à supprimer |

### Tests

- **Aucun répertoire `src/test` dans aucun des deux modules**
- **Aucun test n'existe dans Nucleus**

### nucleus-frontend-sdk

- SDK TypeScript (Angular, React Native, core) — **hors scope backend Kotlin**
- 34 fichiers suivis par git
- Non concerné par le build Gradle

## Packages

| Package | Module | Description |
|---------|--------|-------------|
| `com.inokey.solution.dnk.nucleus.annotation` | core | Annotations `@NucleusOp`, `@MultiPlannerSignature` |
| `com.inokey.solution.dnk.nucleus.contract` | core | Introspection de contrat OpenAPI |
| `com.inokey.solution.dnk.nucleus.core` | core | Constantes de headers HTTP |
| `com.inokey.solution.dnk.nucleus.domain` | core | DOM de métadonnées de requête |
| `com.inokey.solution.dnk.nucleus.enum` | core | Enums métier (AccountType, Audience, AuthProvider, etc.) |
| `com.inokey.solution.dnk.nucleus.error` | core | Handler d'erreurs JSON |
| `com.inokey.solution.dnk.nucleus.filter` | core | Filtre WebFlux signature |
| `com.inokey.solution.dnk.nucleus.logging` | core | Logger de signatures au démarrage |
| `com.inokey.solution.dnk.nucleus.nucleus7` | core | Guards Nucleus7 (consent, safety, latency, principles) |
| `com.inokey.solution.dnk.nucleus.problem` | core | Modèle `Problem` (RFC 9457) |
| `com.inokey.solution.dnk.nucleus.spi` | core | SPI interfaces (ErrorMapper, ObservationContributor, OperationResolver) |
| `com.inokey.solution.dnk.nucleus.observability` | observability | Properties |
| `com.inokey.solution.dnk.nucleus.observability.autoconfigure` | observability | Auto-config, AOP, métriques, catalogue |
| `com.inokey.solution.dnk.nucleus.observability.filter` | observability | WebFilter observabilité |

## Annotations

| Annotation | Package | Cible | Rôle |
|------------|---------|-------|------|
| `@NucleusOp` (core) | `nucleus.annotation` | FUNCTION | Marque une opération observable (code + surface) |
| `@NucleusOp` (observability) | `nucleus.observability.autoconfigure` | FUNCTION | Marque une opération avec `MultiplannerOperation` + extraTags |
| `@MultiPlannerSignature` | `nucleus.annotation` | CLASS | Version et module du contrôleur |
| `@Principles` | `nucleus.nucleus7` | CLASS, FUNCTION | Associe des principes Nucleus7 |
| `@PrincipleNote` | `nucleus.nucleus7` | CLASS, FUNCTION | Note descriptive de principe |

## Filtres WebFlux

| Filtre | Ordre | Module | Rôle |
|--------|-------|--------|------|
| `ConsentGuard` | HIGHEST+10 | core | Bloque écritures sans consent |
| `SafetyShield` | HIGHEST+15 | core | Bloque si safety score < seuil |
| `LatencyBudgetFilter` | HIGHEST+20 | core | Mesure latence, marque dépassements |
| `JsonSchemaValidationFilter` | HIGHEST+25 | core | Stub — validation schéma JSON (TODO) |
| `MultiPlannerSignatureFilter` | HIGHEST+100 | core | Injecte headers de signature versionnelle |
| `ConsentGuardWebFilter` | — | core | @Component — consent guard alternatif |
| `NucleusWebFilter` | HIGHEST+10 | observability | Correlation ID, MDC, operation resolver, timing |

## Auto-configurations

| Auto-configuration | Condition | Beans créés |
|--------------------|-----------|-------------|
| `NucleusObservabilityAutoConfiguration` | `nucleus.enabled=true` (default), REACTIVE web | `NucleusWebFilter`, `NucleusOpAspectProFixedV2`, `NucleusOpsInfoContributor`, `QuotaMetricsService` |
| `Nucleus7Config` | toujours (si MeterRegistry présent) | `ConsentGuard`, `SafetyShield`, `LatencyBudgetFilter`, `JsonSchemaValidationFilter` |

## Propriétés YAML

| Préfixe | Classe | Module |
|---------|--------|--------|
| `nucleus.*` | `NucleusProperties` | observability |
| `nucleus7.consent.*` | `ConsentProperties` | core |
| `nucleus7.safety.*` | `SafetyProps` | core |
| `nucleus7.latency.*` | `LatencyProps` | core |
| `multiplanner.consent.required-version` | `StaticConsentVersionValidator` (@Value) | core |
| `multiplanner.contract.model-package` | `ContractIntrospector` (@Value) | core |

## Dépendances

### nucleus-core

| Scope | Dépendance | Notes |
|-------|-----------|-------|
| api | `spring-boot-dependencies:4.0.6` (BOM) | Source de vérité versions |
| api | `reactor-core` | Reactor |
| api | `spring-context` | Spring |
| api | `spring-web` | Spring Web |
| api | `spring-webflux` | WebFlux |
| api | `micrometer-core` | Métriques |
| api | `slf4j-api` | Logging |
| implementation | `kotlin-reflect` | Reflection Kotlin |
| implementation | `spring-boot-autoconfigure` | Auto-config |
| implementation | `jackson-databind` | JSON |
| implementation | `jackson-module-kotlin` | JSON Kotlin |
| testImplementation | `spring-boot-starter-test` | Tests (aucun test existant) |
| testImplementation | `reactor-test` | Tests reactor (aucun test existant) |
| testImplementation | `kotlin-test-junit5` | Tests Kotlin (aucun test existant) |

### nucleus-observability-spring-boot-starter

| Scope | Dépendance | Notes |
|-------|-----------|-------|
| api | `project(':nucleus-core')` | Dépendance interne |
| api | `spring-boot-dependencies:4.0.6` (BOM) | |
| implementation | `spring-boot-autoconfigure` | |
| compileOnly | `spring-boot-actuator` | InfoContributor |
| implementation | `aspectjweaver` | AOP runtime |
| implementation | `micrometer-observation` | Observation API |
| compileOnly | `reactor-extra` | |
| implementation | `reactor-core-micrometer` | Micrometer + Reactor |
| kapt | `spring-boot-configuration-processor:4.0.6` | Metadata config |
| testImplementation | `spring-boot-starter-test` | Tests (aucun test existant) |
| testImplementation | `kotlin-test-junit5` | Tests (aucun test existant) |

### Dépendances externes

- **Aucune dépendance vers multiplanner-backend-common** — pas de cycle
- **Aucune dépendance vers multiplanner-contracts-openapi**
- **Aucun chemin absolu** dans les fichiers de build
- **Aucun `includeBuild`** dans settings.gradle
- **Aucune dépendance circulaire** détectée

## Build infrastructure

| Élément | Statut | Notes |
|---------|--------|-------|
| `gradle-wrapper.jar` | ✅ présent | |
| `gradle-wrapper.properties` | ❌ absent | **Bloqueur** — doit être créé |
| `gradlew.bat` / `gradlew` | ❌ absent | **Bloqueur** — doit être créé |
| `gradle.properties` | ❌ absent | Optionnel mais recommandé |
| JDK | 25 | `JavaLanguageVersion.of(25)` |
| Kotlin | 2.3.21 | |
| Spring Boot | 4.0.6 | |
| Gradle | inconnu | Pas de wrapper.properties |

## Fichiers générés

- Aucun fichier généré dans le repo (build/ est gitignored)
- `spring-configuration-metadata.json` serait généré par kapt mais n'est pas commité

## Résumé

| Métrique | Valeur |
|----------|--------|
| Modules Gradle | 2 |
| Fichiers Kotlin (core) | 36 |
| Fichiers Kotlin (observability) | 8 |
| Total fichiers Kotlin | 44 |
| Tests | 0 |
| Resources | 1 (AutoConfiguration.imports) |
| Annotations | 5 |
| Enums | 8 |
| Interfaces (SPI) | 3 |
| WebFilters | 7 |
| Auto-configurations | 2 |
| Properties classes | 4 |
| Dépendances vers Common | 0 |
| Dépendances vers Contracts | 0 |
| Chemins absolus | 0 |
| Secrets dans code | 0 |
| Fichiers non suivis | 0 |
| Build wrapper complet | ❌ (jar seul) |
