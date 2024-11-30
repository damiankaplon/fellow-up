package io.fellowup.security

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.net.URI

const val AUTH_CONFIG_OAUTH = "keycloak"
const val AUTH_CONFIG_JWT = "keycloak-jwt"

fun Application.installOAuthAuth() {
    install(Authentication) {
        oauth(AUTH_CONFIG_OAUTH) {
            urlProvider = { "http://localhost:8080/token" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "keycloak",
                    authorizeUrl = "http://localhost:8282/realms/fellow_up/protocol/openid-connect/auth",
                    accessTokenUrl = "http://localhost:8282/realms/fellow_up/protocol/openid-connect/token",
                    requestMethod = HttpMethod.Post,
                    clientId = "app_fellow_up",
                    clientSecret = "7FJwC2xZoqiFdOHCCKNx1SvZ4Hv9GOit"
                )
            }
            client = HttpClient(Apache)
        }

        jwt(AUTH_CONFIG_JWT) {
            val jwkEndpointUrl =
                URI.create("http://localhost:8282/realms/fellow_up/protocol/openid-connect/certs").toURL()
            val jwkProvider = JwkProviderBuilder(jwkEndpointUrl).build()
            verifier(jwkProvider, "http://localhost:8282/realms/fellow_up") {
            }
            validate { jwtCredential -> JWTPrincipal(jwtCredential.payload) }
            challenge { _, _ -> call.respond(HttpStatusCode.Unauthorized) }
        }
    }
}
