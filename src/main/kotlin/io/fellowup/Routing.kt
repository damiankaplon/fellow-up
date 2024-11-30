package io.fellowup

import io.fellowup.security.AUTH_CONFIG_JWT
import io.fellowup.security.registerOAuthCodeFlowEndpoints
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        registerOAuthCodeFlowEndpoints()
        authenticate(AUTH_CONFIG_JWT) {
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }
}
