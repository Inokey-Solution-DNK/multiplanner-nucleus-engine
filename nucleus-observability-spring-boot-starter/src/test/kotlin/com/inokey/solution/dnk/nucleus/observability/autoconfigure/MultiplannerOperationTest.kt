package com.inokey.solution.dnk.nucleus.observability.autoconfigure

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MultiplannerOperationTest {

    @Test
    fun `metricName follows op module action pattern`() {
        assertEquals("op.planiLoisir.activation", MultiplannerOperation.PLANI_LOISIR_ACTIVATE.metricName)
        assertEquals("op.nucleus.probe", MultiplannerOperation.NUCLEUS_PROBE.metricName)
    }

    @Test
    fun `spanName follows module slash action pattern`() {
        assertEquals("planiLoisir/activation", MultiplannerOperation.PLANI_LOISIR_ACTIVATE.spanName)
        assertEquals("nucleus/probe", MultiplannerOperation.NUCLEUS_PROBE.spanName)
    }

    @Test
    fun `otelName follows module dot action pattern`() {
        assertEquals("planiLoisir.activation", MultiplannerOperation.PLANI_LOISIR_ACTIVATE.otelName)
        assertEquals("nucleus.probe", MultiplannerOperation.NUCLEUS_PROBE.otelName)
    }

    @Test
    fun `enum has entries`() {
        assertTrue(MultiplannerOperation.entries.isNotEmpty())
    }
}
