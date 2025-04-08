package io.fellowup.infrastructure.test

import io.fellowup.infrastructure.security.SecuredRouting
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing

const val MOCK_JWT_AUTH_PROVIDER_NAME = "mock-jwt-auth-provider"

internal class MockJwtAuthenticationProvider(
    val config: Config
) : AuthenticationProvider(config), SecuredRouting {
    override suspend fun onAuthenticate(context: AuthenticationContext) {
        config.principal?.let { context.principal(it) }
    }

    override fun invoke(routing: Routing, routes: Route.() -> Unit) {
        routing.authenticate(MOCK_JWT_AUTH_PROVIDER_NAME) { routes() }
    }

    data class Config(
        var principal: JWTPrincipal? = null
    ) : AuthenticationProvider.Config(MOCK_JWT_AUTH_PROVIDER_NAME)
}

