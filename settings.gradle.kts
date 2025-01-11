rootProject.name = "fellow-up"
pluginManagement {
    val flyway_version = extra["flyway_version"] as String
    plugins {
        kotlin("jvm") version "2.1.0"
        kotlin("plugin.serialization") version "2.1.0"
        id("io.ktor.plugin") version "3.0.1"
        id("org.flywaydb.flyway") version flyway_version
    }
}
