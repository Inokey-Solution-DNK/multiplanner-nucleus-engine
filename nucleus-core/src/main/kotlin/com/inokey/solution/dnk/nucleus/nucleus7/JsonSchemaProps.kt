package com.inokey.solution.dnk.nucleus.nucleus7

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("nucleus7.json-schema")
data class JsonSchemaProps(
    val enabled: Boolean = false,
    val devMode: Boolean = false
)
