package com.inokey.solution.dnk.nucleus.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NucleusFilterOrderTest {

    @Test
    fun `all filter orders are unique`() {
        val orders = listOf(
            NucleusFilterOrder.NUCLEUS_WEB_FILTER,
            NucleusFilterOrder.CONSENT_GUARD,
            NucleusFilterOrder.SAFETY_SHIELD,
            NucleusFilterOrder.LATENCY_BUDGET_FILTER,
            NucleusFilterOrder.SIGNATURE_FILTER
        )
        assertEquals(orders.size, orders.toSet().size, "Filter orders must be unique")
    }

    @Test
    fun `nucleus web filter runs first`() {
        assertTrue(NucleusFilterOrder.NUCLEUS_WEB_FILTER < NucleusFilterOrder.CONSENT_GUARD)
        assertTrue(NucleusFilterOrder.CONSENT_GUARD < NucleusFilterOrder.SAFETY_SHIELD)
        assertTrue(NucleusFilterOrder.SAFETY_SHIELD < NucleusFilterOrder.LATENCY_BUDGET_FILTER)
        assertTrue(NucleusFilterOrder.LATENCY_BUDGET_FILTER < NucleusFilterOrder.SIGNATURE_FILTER)
    }
}
