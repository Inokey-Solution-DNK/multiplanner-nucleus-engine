package com.inokey.solution.dnk.nucleus.core

import org.springframework.core.Ordered

/**
 * Ordre unique des WebFilters Nucleus.
 * Aucun filtre ne doit partager le même ordre.
 */
object NucleusFilterOrder {
    const val NUCLEUS_WEB_FILTER      = Ordered.HIGHEST_PRECEDENCE + 0
    const val CONSENT_GUARD           = Ordered.HIGHEST_PRECEDENCE + 10
    const val SAFETY_SHIELD           = Ordered.HIGHEST_PRECEDENCE + 20
    const val LATENCY_BUDGET_FILTER   = Ordered.HIGHEST_PRECEDENCE + 30
    const val SIGNATURE_FILTER        = Ordered.HIGHEST_PRECEDENCE + 100
}
