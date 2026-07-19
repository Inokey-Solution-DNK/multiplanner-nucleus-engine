package com.inokey.solution.dnk.nucleus.domain

import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RequestMetaDomStabilityTest {

    @Test
    fun `resolvedCorrelationId returns same UUID on repeated calls`() {
        val dom = RequestMetaDom(consentVersion = "v1.0")
        assertNull(dom.correlationId)
        val first = dom.resolvedCorrelationId()
        val second = dom.resolvedCorrelationId()
        assertNotNull(first)
        assertEquals(first, second, "resolvedCorrelationId must be stable across calls")
    }

    @Test
    fun `correlationIdString matches resolvedCorrelationId`() {
        val dom = RequestMetaDom(consentVersion = "v1.0")
        assertEquals(dom.resolvedCorrelationId().toString(), dom.correlationIdString())
    }
}
