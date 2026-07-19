package com.inokey.solution.dnk.nucleus.nucleus7

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConsentGuardTest {

    private val meter = SimpleMeterRegistry()
    private val props = ConsentProperties(enabled = true, requiredOnWrite = true, whitelistPaths = listOf("/actuator/**"))
    private val validator = StaticConsentVersionValidator("v1.0")
    private val guard = ConsentGuard(props, validator, meter)

    @Test
    fun `disabled guard passes through`() {
        val disabledProps = ConsentProperties(enabled = false)
        val disabledGuard = ConsentGuard(disabledProps, null, meter)
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/api/data").build()
        )
        StepVerifier.create(disabledGuard.filter(exchange) { Mono.empty() })
            .verifyComplete()
    }

    @Test
    fun `write without consent header is rejected`() {
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/api/data").build()
        )
        StepVerifier.create(guard.filter(exchange) { Mono.empty() })
            .verifyComplete()
        assertEquals(400, exchange.response.statusCode!!.value())
        assertTrue(meter.counter("nucleus7.consent.missing").count() > 0)
    }

    @Test
    fun `write with valid consent passes`() {
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/api/data")
                .header("X-Consent-Version", "v1.0")
                .build()
        )
        StepVerifier.create(guard.filter(exchange) { Mono.empty() })
            .verifyComplete()
        assertTrue(meter.counter("nucleus7.consent.accepted").count() > 0)
    }

    @Test
    fun `write with invalid consent is rejected`() {
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/api/data")
                .header("X-Consent-Version", "v9.9")
                .build()
        )
        StepVerifier.create(guard.filter(exchange) { Mono.empty() })
            .verifyComplete()
        assertEquals(400, exchange.response.statusCode!!.value())
        assertTrue(meter.counter("nucleus7.consent.rejected").count() > 0)
    }

    @Test
    fun `GET without consent passes`() {
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/data").build()
        )
        StepVerifier.create(guard.filter(exchange) { Mono.empty() })
            .verifyComplete()
    }

    @Test
    fun `whitelisted path passes without consent`() {
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/actuator/health").build()
        )
        StepVerifier.create(guard.filter(exchange) { Mono.empty() })
            .verifyComplete()
    }

    @Test
    fun `guard without validator accepts any non-blank header`() {
        val noValidatorGuard = ConsentGuard(props, null, meter)
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/api/data")
                .header("X-Consent-Version", "anything")
                .build()
        )
        StepVerifier.create(noValidatorGuard.filter(exchange) { Mono.empty() })
            .verifyComplete()
        assertTrue(meter.counter("nucleus7.consent.accepted").count() > 0)
    }
}
