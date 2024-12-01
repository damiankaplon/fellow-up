package io.fellowup.security

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.registerOAuthCodeFlowEndpoints(oauthLogoutUrl: String? = null) {
    authenticate(OAUTH2_SERVER_CONFIG) {
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
    oauthLogoutUrl?.run { get("logout") { call.respondRedirect(this@run) } }
}

fun Routing.secure(routing: Route.() -> Unit) {
    authenticate(JWT_CONFIG) {
        routing.invoke(this)
    }
}
