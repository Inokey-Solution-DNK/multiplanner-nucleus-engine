package com.inokey.solution.dnk.nucleus.observability.autoconfigure

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class QuotaMetricsServiceTest {

    private val meterRegistry = SimpleMeterRegistry()
    private val service = QuotaMetricsService(meterRegistry)

    @Test
    fun `updateQuotaRemaining registers gauge`() {
        service.updateQuotaRemaining("OP_TEST", "FREE", 42L, 100)
        val gauge = meterRegistry.find("multiplanner_quota_remaining").gauge()
        assertNotNull(gauge)
        assertEquals(100.0, gauge.value())
    }

    @Test
    fun `incrementQuotaConsumed registers counter`() {
        service.incrementQuotaConsumed("OP_TEST", "FREE", 5)
        val counter = meterRegistry.find("multiplanner_quota_consumed_total").counter()
        assertNotNull(counter)
        assertEquals(5.0, counter.count())
    }

    @Test
    fun `getQuotaSummary returns correct values`() {
        service.updateQuotaRemaining("OP_SUM", "FREE", 1L, 50)
        service.updateQuotaRemaining("OP_SUM", "PRO", 2L, 30)
        val summary = service.getQuotaSummary("OP_SUM")
        assertEquals("OP_SUM", summary.operation)
        assertEquals(80, summary.totalRemaining)
        assertEquals(2, summary.activeAccounts)
    }
}
