package io.fellowup

import io.fellowup.security.OAuthPropertiesConfigFileProvider
import io.fellowup.security.registerOAuthCodeFlowEndpoints
import io.fellowup.security.secure
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        /* Room for improvement. DI? */
        val oauthPropertiesProvider = OAuthPropertiesConfigFileProvider(this@configureRouting.environment)
        registerOAuthCodeFlowEndpoints(oauthLogoutUrl = oauthPropertiesProvider.logoutUrl)
        secure {
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }
}
