package com.inokey.solution.dnk.nucleus.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NucleusHeadersTest {

    @Test
    fun `headers have correct names`() {
        assertEquals("X-Correlation-Id", NucleusHeaders.CORRELATION_ID)
        assertEquals("X-Session-Id", NucleusHeaders.SESSION_ID)
        assertEquals("X-Consent-Version", NucleusHeaders.CONSENT_VERSION)
        assertEquals("Idempotency-Key", NucleusHeaders.IDEMPOTENCY_KEY)
        assertEquals("X-Request-Timing", NucleusHeaders.REQUEST_TIMING)
    }
}
