package com.inokey.solution.dnk.nucleus.nucleus7

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("nucleus7.latency")
data class LatencyProps(
    val enabled: Boolean = false,
    val budgetMs: Long = 250,
    @Deprecated("hardBlock is no longer supported. LatencyBudgetFilter is observational only.", level = DeprecationLevel.WARNING)
    val hardBlock: Boolean = false
)

