package com.inokey.solution.dnk.nucleus.nucleus7

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Test
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SafetyShieldTest {

    private val meter = SimpleMeterRegistry()
    private val props = SafetyProps(enabled = true, minScore = 0.80, enforcePaths = listOf("/api/**"))

    @Test
    fun `disabled shield passes through`() {
        val disabledProps = SafetyProps(enabled = false)
        val shield = SafetyShield(disabledProps, null, meter)
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/data").build()
        )
        StepVerifier.create(shield.filter(exchange) { Mono.empty() })
            .verifyComplete()
    }

    @Test
    fun `path not in enforcePaths passes`() {
        val shield = SafetyShield(props, null, meter)
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/public/health").build()
        )
        StepVerifier.create(shield.filter(exchange) { Mono.empty() })
            .verifyComplete()
    }

    @Test
    fun `no resolver blocks request`() {
        val shield = SafetyShield(props, null, meter)
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/data").build()
        )
        StepVerifier.create(shield.filter(exchange) { Mono.empty() })
            .verifyComplete()
        assertEquals(403, exchange.response.statusCode!!.value())
        assertTrue(meter.counter("nucleus7.safety.blocked").count() > 0)
    }

    @Test
    fun `resolver returns low score blocks`() {
        val lowScoreResolver = object : NucleusSafetyScoreResolver {
            override fun resolve(exchange: org.springframework.web.server.ServerWebExchange): Mono<Double> = Mono.just(0.5)
        }
        val shield = SafetyShield(props, lowScoreResolver, meter)
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/data").build()
        )
        StepVerifier.create(shield.filter(exchange) { Mono.empty() })
            .verifyComplete()
        assertEquals(403, exchange.response.statusCode!!.value())
    }

    @Test
    fun `resolver returns high score passes`() {
        val highScoreResolver = object : NucleusSafetyScoreResolver {
            override fun resolve(exchange: org.springframework.web.server.ServerWebExchange): Mono<Double> = Mono.just(0.95)
        }
        val shield = SafetyShield(props, highScoreResolver, meter)
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/data").build()
        )
        StepVerifier.create(shield.filter(exchange) { Mono.empty() })
            .verifyComplete()
    }

    @Test
    fun `resolver returns empty blocks`() {
        val emptyResolver = object : NucleusSafetyScoreResolver {
            override fun resolve(exchange: org.springframework.web.server.ServerWebExchange): Mono<Double> = Mono.empty()
        }
        val shield = SafetyShield(props, emptyResolver, meter)
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/data").build()
        )
        StepVerifier.create(shield.filter(exchange) { Mono.empty() })
            .verifyComplete()
        assertEquals(403, exchange.response.statusCode!!.value())
    }
}
