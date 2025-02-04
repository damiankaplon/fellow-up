package io.fellowup.security

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.URI

private const val OAUTH2_SERVER_CONFIG = "oauth2-server-config"
private const val JWT_CONFIG = "jwt-config"

class OAuthConfigFileProvider(private val env: ApplicationEnvironment) {
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

fun Application.installOAuthAuth(): OAuthAuthModule {
        val properties = OAuthConfigFileProvider(this@installOAuthAuth.environment)
    install(Authentication) {
        oauth(OAUTH2_SERVER_CONFIG) {
            urlProvider = { properties.redirectUrl }
            client = HttpClient(Apache)
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
        this.routing { installOauthCodeFlow(properties.logoutUrl) }
    return OAuthAuthModule(SecuredRouting { routing, routes -> routing.authenticate(JWT_CONFIG) { routes() } })
}

private fun Routing.installOauthCodeFlow(oauthLogoutUrl: String? = null) {
    authenticate(OAUTH2_SERVER_CONFIG) {
        get("/api/token/keycloak") {
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

class OAuthAuthModule(
    val securedRouting: SecuredRouting
)
