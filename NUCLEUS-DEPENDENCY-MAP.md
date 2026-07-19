# NUCLEUS-DEPENDENCY-MAP.md

## Settings

- **Fichier**: `settings.gradle`
- **Root project**: `nucleus`
- **Modules inclus**:
  - `nucleus-core`
  - `nucleus-observability-spring-boot-starter`
- **includeBuild**: aucun
- **Chemins absolus**: aucun

## Coordonnées Maven

| Module | groupId | artifactId | version |
|--------|---------|-----------|---------|
| nucleus-core | com.inokey.solution.dnk | nucleus-core | 1.0.0-SNAPSHOT |
| nucleus-observability-spring-boot-starter | com.inokey.solution.dnk | nucleus-observability-spring-boot-starter | 1.0.0-SNAPSHOT |

## Build root

| Élément | Valeur |
|---------|--------|
| Kotlin | 2.3.21 |
| JDK | 25 |
| Spring Boot BOM | 4.0.6 |
| Gradle wrapper | **INCOMPLET** (jar sans properties ni gradlew) |
| OWASP dependency-check | 12.2.1 |
| CycloneDX BOM | 3.2.4 |
| OpenRewrite Spring Boot 4 | 7.32.2 |

## Dépendances inter-modules

```
nucleus-observability-spring-boot-starter
  └── api project(':nucleus-core')
```

- **Sens**: observability → core (unidirectionnel)
- **Cycle**: aucun

## Dépendances externes — nucleus-core

| Scope | Dépendance | Version gérée par |
|-------|-----------|-------------------|
| api | `org.springframework.boot:spring-boot-dependencies:4.0.6` (BOM) | Explicite |
| api | `io.projectreactor:reactor-core` | BOM |
| api | `org.springframework:spring-context` | BOM |
| api | `org.springframework:spring-web` | BOM |
| api | `org.springframework:spring-webflux` | BOM |
| api | `io.micrometer:micrometer-core` | BOM |
| api | `org.slf4j:slf4j-api` | BOM |
| implementation | `org.jetbrains.kotlin:kotlin-reflect` | Kotlin plugin |
| implementation | `org.springframework.boot:spring-boot-autoconfigure` | BOM |
| implementation | `com.fasterxml.jackson.core:jackson-databind` | BOM |
| implementation | `com.fasterxml.jackson.module:jackson-module-kotlin` | BOM |
| testImplementation | `org.springframework.boot:spring-boot-starter-test` | BOM |
| testImplementation | `io.projectreactor:reactor-test` | BOM |
| testImplementation | `org.jetbrains.kotlin:kotlin-test-junit5` | Kotlin plugin |

## Dépendances externes — nucleus-observability-spring-boot-starter

| Scope | Dépendance | Version gérée par |
|-------|-----------|-------------------|
| api | `project(':nucleus-core')` | — |
| api | `org.springframework.boot:spring-boot-dependencies:4.0.6` (BOM) | Explicite |
| implementation | `org.springframework.boot:spring-boot-autoconfigure` | BOM |
| compileOnly | `org.springframework.boot:spring-boot-actuator` | BOM |
| implementation | `org.aspectj:aspectjweaver` | BOM |
| implementation | `io.micrometer:micrometer-observation` | BOM |
| compileOnly | `io.projectreactor.addons:reactor-extra` | BOM |
| implementation | `io.projectreactor:reactor-core-micrometer` | BOM |
| kapt | `org.springframework.boot:spring-boot-configuration-processor:4.0.6` | Explicite |
| testImplementation | `org.springframework.boot:spring-boot-starter-test` | BOM |
| testImplementation | `org.jetbrains.kotlin:kotlin-test-junit5` | Kotlin plugin |

## Dépendances vers Common

- **Aucune** — Nucleus ne dépend pas de multiplanner-backend-common

## Dépendances vers Contracts

- **Aucune** — Nucleus ne dépend pas de multiplanner-contracts-openapi

## Dépendances circulaires

- **Aucune détectée**
- Common importe `Problem` depuis Nucleus (via import Kotlin), mais n'a pas de dépendance Gradle déclarée vers Nucleus
- **Note**: Common utilise `Problem` via `includeBuild` ou dépendance mavenLocal non déclarée dans son build.gradle — à vérifier

## Exclusions de sécurité

| Groupe exclu | Module | Raison |
|---------------|--------|--------|
| org.apache.logging.log4j | log4j-core | Log4Shell — Nucleus utilise SLF4J + Logback |
| org.apache.logging.log4j | log4j-api | Idem |
| org.apache.logging.log4j | log4j-slf4j2-impl | Idem |
| org.springframework.boot | spring-boot-starter-log4j2 | Idem |

## Repositories

- `mavenCentral()` (tous les sous-projets)

## Publication

- **Type**: `maven-publish`
- **Publication**: `mavenJava` depuis `components.java`
- **withSourcesJar**: oui (configuré dans `java {}`)

## Problèmes identifiés

| # | Problème | Sévérité | Solution |
|---|---------|----------|----------|
| 1 | Pas de `gradle-wrapper.properties` | **BLOQUEUR** | Créer avec Gradle 9.4.1 (aligné avec Common) |
| 2 | Pas de `gradlew.bat` / `gradlew` | **BLOQUEUR** | Générer avec `gradle wrapper` |
| 3 | Pas de `gradle.properties` | Mineur | Créer avec `org.gradle.jvmargs=-Xmx2g` |
| 4 | Aucun test | **BLOQUEUR** pour NUCLEUS_READY | Créer tests minimaux |
| 5 | `StaticConsentVersionValidator` requiert propriété obligatoire sans défaut | Mineur | Ajouter défaut ou `@ConditionalOnProperty` |
| 6 | `ConsentGuardWebFilter` duplique `ConsentGuard` | Mineur | Marquer DEPRECATED ou supprimer |
| 7 | `.cleanup` et `CLEANUP_LOG.md` dans source | Mineur | Supprimer |
| 8 | `reactor-extra` en `compileOnly` mais potentiellement requis au runtime | Mineur | Vérifier usage effectif |

## Verdict

| Critère | Statut |
|---------|--------|
| Pas de dépendance circulaire | ✅ PASS |
| Pas de chemin absolu | ✅ PASS |
| Pas de dépendance vers Common | ✅ PASS |
| Pas de dépendance vers Contracts | ✅ PASS |
| Build wrapper complet | ❌ FAIL |
| Publication Maven configurée | ✅ PASS |
| Reproductibilité | ❌ FAIL (wrapper incomplet) |
