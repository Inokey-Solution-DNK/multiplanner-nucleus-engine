package com.inokey.solution.dnk.nucleus.nucleus7

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class StaticConsentVersionValidatorTest {

    @Test
    fun `accepts matching version`() {
        val validator = StaticConsentVersionValidator("v1.0")
        assertTrue(validator.isAccepted("v1.0"))
        assertTrue(validator.isAccepted("  v1.0  "))
    }

    @Test
    fun `rejects non-matching version`() {
        val validator = StaticConsentVersionValidator("v1.0")
        assertFalse(validator.isAccepted("v2.0"))
        assertFalse(validator.isAccepted("v1.1"))
    }
}
