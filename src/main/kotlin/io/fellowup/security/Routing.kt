package io.fellowup.security

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.registerOAuthCodeFlowEndpoints() {
    authenticate(AUTH_CONFIG_OAUTH) {
        get("/login") { /* This route is just registered to trigger OAuth2 */ }
        get("/token") {
            val token = call.principal<OAuthAccessTokenResponse.OAuth2>()
            if (token == null) {
                call.respond(HttpStatusCode.Unauthorized); return@get
            }
            call.respond(
                HttpStatusCode.OK,
                OAuthAuth(
                    token = token.accessToken,
                    refreshToken = token.refreshToken,
                    expiresIn = token.expiresIn.toInt()
                )
            )
        }
    }
    get("logout") {
        call.respondRedirect("http://localhost:8282/realms/fellow_up/protocol/openid-connect/logout")
    }
}

fun Routing.secure(routing: Route.() -> Unit) {
    authenticate(AUTH_CONFIG_JWT) {
        routing.invoke(this)
    }
}
