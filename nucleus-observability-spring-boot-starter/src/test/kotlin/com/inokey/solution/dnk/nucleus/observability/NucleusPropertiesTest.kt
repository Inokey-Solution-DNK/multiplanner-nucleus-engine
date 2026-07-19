package com.inokey.solution.dnk.nucleus.observability

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NucleusPropertiesTest {

    @Test
    fun `default values are safe`() {
        val props = NucleusProperties()
        assertTrue(props.enabled)
        assertEquals("unknown", props.applicationCode)
        assertEquals("INFO", props.logLevel)
        assertEquals(5000L, props.latencyBudgetMs)
        assertEquals(0.80, props.defaultSafetyScoreThreshold)
        assertTrue(props.tracingEnabled)
        assertTrue(props.metricsEnabled)
    }

    @Test
    fun `observability defaults`() {
        val obs = NucleusProperties.ObservabilityProperties()
        assertTrue(obs.enabled)
        assertEquals("X-Correlation-Id", obs.correlationHeader)
        assertEquals("X-Session-Id", obs.sessionHeader)
        assertTrue(!obs.captureRequestBody)
        assertTrue(!obs.captureResponseBody)
    }

    @Test
    fun `guard defaults`() {
        val guard = NucleusProperties.GuardProperties()
        assertTrue(guard.consentCheckEnabled)
        assertTrue(!guard.safetyCheckEnabled)
        assertEquals(0.80, guard.safetyScoreThreshold)
        assertEquals(5000L, guard.latencyBudgetMs)
        assertTrue(guard.metricsEnabled)
    }
}
