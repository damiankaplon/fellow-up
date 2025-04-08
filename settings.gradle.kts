pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.0"
        kotlin("plugin.serialization") version "2.1.0"
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "fellow-up"
include("domain")
include("infrastructure")
