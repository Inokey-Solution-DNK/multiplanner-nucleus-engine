package com.inokey.solution.dnk.nucleus.enum

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ConstantHeaderTest {

    @Test
    fun `core headers have stable names`() {
        assertEquals("X-Correlation-Id", ConstantHeader.CORRELATION_ID)
        assertEquals("X-Session-Id", ConstantHeader.SESSION_ID)
        assertEquals("X-Consent-Version", ConstantHeader.CONSENT_VERSION)
        assertEquals("Idempotency-Key", ConstantHeader.IDEMPOTENCY_KEY)
        assertEquals("X-Request-Timing", ConstantHeader.REQUEST_TIMING)
    }

    @Test
    fun `NucleusHeader delegates to ConstantHeader`() {
        assertEquals(ConstantHeader.CORRELATION_ID, NucleusHeader.CORRELATION_ID.headerName)
        assertEquals(ConstantHeader.SESSION_ID, NucleusHeader.SESSION_ID.headerName)
        assertEquals(ConstantHeader.REQUEST_TIMING, NucleusHeader.REQUEST_TIMING.headerName)
    }

    @Test
    fun `NucleusHeader from returns enum for known header`() {
        assertNotNull(NucleusHeader.from("X-Correlation-Id"))
        assertNotNull(NucleusHeader.from("x-correlation-id"))
    }

    @Test
    fun `NucleusHeader from returns null for unknown header`() {
        assertNull(NucleusHeader.from("X-Unknown-Header"))
        assertNull(NucleusHeader.from(null))
    }
}
