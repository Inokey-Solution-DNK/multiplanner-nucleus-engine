package com.inokey.solution.dnk.nucleus.problem

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

class ProblemTest {

    @Test
    fun `Problem creates with defaults`() {
        val p = Problem(code = "NOT_FOUND", message = "Ressource introuvable.")
        assertEquals("NOT_FOUND", p.code)
        assertEquals("Ressource introuvable.", p.message)
        assertEquals("", p.path)
        assertTrue(p.errorId.isNotBlank())
        assertTrue(p.details.isEmpty())
        assertNotNull(p.timestamp)
    }

    @Test
    fun `Problem with full constructor`() {
        val p = Problem(
            code = "BAD_REQUEST",
            message = "Invalid",
            path = "/api/test",
            errorId = "err-123",
            details = listOf(mapOf("field" to "name"))
        )
        assertEquals("/api/test", p.path)
        assertEquals("err-123", p.errorId)
        assertEquals(1, p.details.size)
    }
}
