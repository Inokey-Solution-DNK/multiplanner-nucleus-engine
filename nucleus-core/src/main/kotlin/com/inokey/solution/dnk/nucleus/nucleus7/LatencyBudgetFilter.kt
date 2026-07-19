package com.inokey.solution.dnk.nucleus.nucleus7

import com.inokey.solution.dnk.nucleus.core.NucleusFilterOrder
import com.inokey.solution.dnk.nucleus.enum.ConstantHeader
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Order(NucleusFilterOrder.LATENCY_BUDGET_FILTER)
@Principles(Principle.ACTION, Principle.PROTECTION)
@PrincipleNote("Mesure la latence et marque les dépassements de budget (performance + protection SLO)")
class LatencyBudgetFilter(
    private val props: LatencyProps,
    meter: MeterRegistry
) : WebFilter {

    private val log = LoggerFactory.getLogger(javaClass)
    private val over = meter.counter("nucleus7.latency.overbudget")

    override fun filter(ex: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (!props.enabled) return chain.filter(ex)
        val start = System.nanoTime()

        ex.response.beforeCommit {
            val ms = (System.nanoTime() - start) / 1_000_000
            if (ms > props.budgetMs) {
                over.increment()
                ex.response.headers.add(ConstantHeader.LATENCY_OVERBUDGET, "true")
                log.warn("[LATENCY_OVERBUDGET] {}ms > {}ms budget", ms, props.budgetMs)
            }
            Mono.empty()
        }
        return chain.filter(ex)
    }
}
