package com.inokey.solution.dnk.nucleus.domain

import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RequestMetaDomTest {

    @Test
    fun `resolvedCorrelationId generates UUID when null`() {
        val dom = RequestMetaDom(consentVersion = "v1.0")
        assertNull(dom.correlationId)
        val resolved = dom.resolvedCorrelationId()
        assertNotNull(resolved)
    }

    @Test
    fun `resolvedCorrelationId returns existing UUID`() {
        val uuid = UUID.randomUUID()
        val dom = RequestMetaDom(correlationId = uuid, consentVersion = "v1.0")
        assertEquals(uuid, dom.resolvedCorrelationId())
        assertEquals(uuid.toString(), dom.correlationIdString())
    }
}
