package com.inokey.solution.dnk.nucleus.autoconfigure

import com.inokey.solution.dnk.nucleus.contract.ContractIntrospector
import com.inokey.solution.dnk.nucleus.filter.MultiPlannerSignatureFilter
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner
import org.assertj.core.api.Assertions.assertThat

class NucleusCoreAutoConfigurationStartupTest {
    @Test
    fun `final Kotlin Core configuration starts without CGLIB proxies`() {
        ReactiveWebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(NucleusCoreAutoConfiguration::class.java))
            .run { context ->
                assertThat(context).hasNotFailed()
                    .hasSingleBean(MultiPlannerSignatureFilter::class.java)
                    .hasSingleBean(ContractIntrospector::class.java)
            }
    }
}
