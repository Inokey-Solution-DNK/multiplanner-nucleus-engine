package com.inokey.solution.dnk.nucleus.enum

/**
 * 🌐 Liste des en-têtes HTTP utilisés dans le noyau Nucleus7.
 * Chaque valeur représente un "édonyme" standard de communication entre filtres, contrôleurs et clients.
 *
 * Les valeurs sont déléguées vers [ConstantHeader] qui est la source canonique unique.
 */
enum class NucleusHeader(val headerName: String) {

    // 🔹 LIFE – consentement / privacy
    CONSENT_VERSION(ConstantHeader.CONSENT_VERSION),

    // 🔹 ACTION – performance / SLO
    LATENCY_OVERBUDGET(ConstantHeader.LATENCY_OVERBUDGET),
    LATENCY_BUDGET(ConstantHeader.LATENCY_BUDGET),

    // 🔹 PROTECTION – sécurité / justice
    SAFETY_SCORE(ConstantHeader.SAFETY_SCORE),
    SECURITY_TOKEN(ConstantHeader.SECURITY_TOKEN),
    REQUEST_SIGNATURE(ConstantHeader.REQUEST_SIGNATURE),

    // 🔹 INTELLECT – traçabilité / explications
    TRACE_ID(ConstantHeader.TRACE_ID),
    CORRELATION_ID(ConstantHeader.CORRELATION_ID),
    SESSION_ID(ConstantHeader.SESSION_ID),
    REQUEST_TIMING(ConstantHeader.REQUEST_TIMING),

    // 🔹 LOVE – accessibilité / équité
    USER_CONTEXT(ConstantHeader.USER_CONTEXT),

    // 🔹 IMAGINATION – expérimentation / diversité
    EXPERIMENT_ID(ConstantHeader.EXPERIMENT_ID),

    // 🔹 EMOTION – ton / personnalisation
    USER_MOOD(ConstantHeader.USER_MOOD),

    // 🔹 VERSIONING – signature versionnelle MultiPlanner
    MULTIPLANNER_VERSION(ConstantHeader.MULTIPLANNER_VERSION),
    MULTIPLANNER_MODULE(ConstantHeader.MULTIPLANNER_MODULE),
    MULTIPLANNER_VENDOR(ConstantHeader.MULTIPLANNER_VENDOR),
    MULTIPLANNER_TIMESTAMP(ConstantHeader.MULTIPLANNER_TIMESTAMP);

    companion object {
        /** Permet de retrouver un enum à partir du nom du header */
        fun from(name: String?): NucleusHeader? =
            NucleusHeader.entries.find { it.headerName.equals(name, ignoreCase = true) }
    }
}

