val kotlin_version: String by project
val logback_version: String by project
val postgresql_version: String by project
val exposed_version: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    id("org.flywaydb.flyway")
}

group = "io.cruxvelo.fellowup"
version = "0.0.1"


buildscript {
    dependencies {
        val flyway_version: String by project
        classpath("org.flywaydb:flyway-database-postgresql:$flyway_version")
        val postgresql_version: String by project
        classpath("org.postgresql:postgresql:$postgresql_version")
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
    implementation("io.ktor:ktor-server-config-yaml-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("io.ktor:ktor-client-apache-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("org.postgresql:postgresql:$postgresql_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("ch.qos.logback:logback-classic:$logback_version")

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
