val kotlin_version: String by project
val logback_version: String by project
val flyway_version: String by project

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("io.ktor.plugin") version "3.0.1"
    id("org.flywaydb.flyway") version "11.1.1"
}

group = "io.cruxvelo.fellowup"
version = "0.0.1"


buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:11.1.1")
        classpath("org.postgresql:postgresql:42.7.4")
    }
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("io.ktor:ktor-client-apache-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")

    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

flyway {
    driver = "org.postgresql.Driver"
    url = getEnvOrProperty("FLYWAY_DB_URL") ?: "jdbc:postgresql://localhost:5432/app_fellow_up"
    user = getEnvOrProperty("FLYWAY_DB_USER") ?: "app_fellow_up"
    password = getEnvOrProperty("FLYWAY_DB_PASSWORD") ?: "app_fellow_up"
    baselineOnMigrate = true
}

fun getEnvOrProperty(value: String): String? = System.getenv(value) ?: System.getProperty(value)
