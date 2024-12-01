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

const val OAUTH2_SERVER_CONFIG = "oauth2-server-config"
const val JWT_CONFIG = "jwt-config"

class OAuthPropertiesConfigFileProvider(private val env: ApplicationEnvironment) {
    val authUrl get() = env.config.property("oauth.auth-url").getString()
    val redirectUrl get() = env.config.property("oauth.redirect-url").getString()
    val accessTokenUrl get() = env.config.property("oauth.access-token-url").getString()
    val clientId get() = env.config.property("oauth.client-id").getString()
    val secret get() = env.config.property("oauth.secret").getString()
    val logoutUrl get() = env.config.propertyOrNull("oauth.logout.url")?.getString()
}

class JwtPropertiesConfigFileProvider(private val env: ApplicationEnvironment) {
    val jwkUrl get() = env.config.property("jwt.jwk-url").getString()
    val issuer get() = env.config.property("jwt.issuer").getString()
}

fun Application.installOAuthAuth() {
    install(Authentication) {
        oauth(OAUTH2_SERVER_CONFIG) {
            val properties = OAuthPropertiesConfigFileProvider(this@installOAuthAuth.environment)
            urlProvider = { properties.redirectUrl }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "keycloak",
                    authorizeUrl = properties.authUrl,
                    accessTokenUrl = properties.accessTokenUrl,
                    requestMethod = HttpMethod.Post,
                    clientId = properties.clientId,
                    clientSecret = properties.secret
                )
            }
            client = HttpClient(Apache)
        }

        jwt(JWT_CONFIG) {
            val properties = JwtPropertiesConfigFileProvider(this@installOAuthAuth.environment)
            val jwkEndpointUrl =
                URI.create(properties.jwkUrl).toURL()
            val jwkProvider = JwkProviderBuilder(jwkEndpointUrl).build()
            verifier(jwkProvider, properties.issuer) { /* No additional constraints */ }
            validate { jwtCredential -> JWTPrincipal(jwtCredential.payload) }
            challenge { _, _ -> call.respond(HttpStatusCode.Unauthorized) }
        }
    }
}
