package com.inokey.solution.dnk.nucleus.nucleus7

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class PrincipleTest {

    @Test
    fun `Principle enum has 7 values`() {
        assertEquals(7, Principle.entries.size)
        assertTrue(Principle.LIFE in Principle.entries)
        assertTrue(Principle.PROTECTION in Principle.entries)
    }
}
