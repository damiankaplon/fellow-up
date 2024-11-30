package io.fellowup

import io.fellowup.security.installOAuthAuth
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    installSerialization()
    installOAuthAuth()
    configureRouting()
}
