package com.inokey.solution.dnk.nucleus.nucleus7

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Test
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LatencyBudgetFilterTest {

    private val meter = SimpleMeterRegistry()

    @Test
    fun `disabled filter passes through`() {
        val props = LatencyProps(enabled = false)
        val filter = LatencyBudgetFilter(props, meter)
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/data").build()
        )
        StepVerifier.create(filter.filter(exchange) { Mono.empty() })
            .verifyComplete()
        assertNull(exchange.response.headers.getFirst("X-Latency-Overbudget"))
    }

    @Test
    fun `fast request does not mark overbudget`() {
        val props = LatencyProps(enabled = true, budgetMs = 5000)
        val filter = LatencyBudgetFilter(props, meter)
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/data").build()
        )
        StepVerifier.create(filter.filter(exchange) { Mono.empty() })
            .verifyComplete()
        assertNull(exchange.response.headers.getFirst("X-Latency-Overbudget"))
        assertEquals(0.0, meter.counter("nucleus7.latency.overbudget").count())
    }

    @Test
    fun `hardBlock property is deprecated but still parseable`() {
        @Suppress("DEPRECATION")
        val props = LatencyProps(enabled = true, budgetMs = 100, hardBlock = true)
        assertEquals(true, props.hardBlock)
    }
}
