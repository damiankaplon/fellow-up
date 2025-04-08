package io.fellowup.infrastructure.test.kafka

import java.util.*

internal object ApplicationIntegrationTestPropertiesLoader {

    val PROPERTIES = Properties()

    init {
        PROPERTIES.load(object {}.javaClass.classLoader.getResourceAsStream("application-integration.properties"))
    }
}
