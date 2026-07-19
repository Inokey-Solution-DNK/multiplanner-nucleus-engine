package com.inokey.solution.dnk.nucleus.enum

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class EnumDecodeTest {

    @Test
    fun `AccountType decode`() {
        assertEquals(AccountType.PERSON, AccountType.decode("PERSON"))
        assertEquals(AccountType.ORGANIZATION, AccountType.decode("organization"))
        assertNull(AccountType.decode(null))
        assertNull(AccountType.decode("INVALID"))
        assertEquals(AccountType.PERSON, AccountType.decodeOrDefault(null))
        assertTrue(AccountType.ORGANIZATION.isOrganization())
        assertTrue(!AccountType.PERSON.isOrganization())
    }

    @Test
    fun `Audience decode`() {
        assertEquals(Audience.B2C, Audience.decode("B2C"))
        assertEquals(Audience.B2B, Audience.decode("b2b"))
        assertFailsWith<IllegalArgumentException> { Audience.decode(null) }
        assertFailsWith<IllegalArgumentException> { Audience.decode("INVALID") }
    }

    @Test
    fun `AuthProvider fromString and fromIssuer`() {
        assertEquals(AuthProvider.KEYCLOAK, AuthProvider.fromString("keycloak"))
        assertEquals(AuthProvider.KEYCLOAK, AuthProvider.fromString(null))
        assertEquals(AuthProvider.KEYCLOAK, AuthProvider.fromIssuer("https://keycloak.local/realms/test"))
        assertEquals(AuthProvider.TEST, AuthProvider.fromIssuer("https://test.local"))
    }

    @Test
    fun `OriginIdp fromKeycloakClaim`() {
        assertEquals(OriginIdp.LOCAL, OriginIdp.fromKeycloakClaim(null))
        assertEquals(OriginIdp.LOCAL, OriginIdp.fromKeycloakClaim(""))
        assertEquals(OriginIdp.GOOGLE, OriginIdp.fromKeycloakClaim("google"))
        assertEquals(OriginIdp.UNKNOWN, OriginIdp.fromKeycloakClaim("nonexistent"))
    }

    @Test
    fun `PolicyEffect fromString`() {
        assertEquals(PolicyEffect.ALLOW, PolicyEffect.fromString("ALLOW"))
        assertEquals(PolicyEffect.DENY, PolicyEffect.fromString("DENY"))
        assertEquals(PolicyEffect.DENY, PolicyEffect.fromString("UNKNOWN"))
    }

    @Test
    fun `NucleusHeader from name`() {
        assertEquals(NucleusHeader.CORRELATION_ID, NucleusHeader.from("X-Correlation-Id"))
        assertEquals(NucleusHeader.CONSENT_VERSION, NucleusHeader.from("x-consent-version"))
        assertNull(NucleusHeader.from("X-Nonexistent"))
        assertNull(NucleusHeader.from(null))
    }
}
