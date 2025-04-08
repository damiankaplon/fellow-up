package io.fellowup.infrastructure.test.matchmaking

import io.fellowup.domain.matchmaking.MatchmakingRepository
import io.fellowup.domain.test.fixtures.db.MockTransactionalRunner
import io.fellowup.domain.test.fixtures.events.NopEventPublisher
import io.fellowup.infrastructure.installAppRouting
import io.fellowup.infrastructure.installSerialization
import io.fellowup.infrastructure.matchmaking.infra.createMatchmakingModule
import io.fellowup.infrastructure.test.MockJwtAuthenticationProvider
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import java.util.*

internal class MatchmakingsTestApp(
    val matchmakingRepository: MatchmakingRepository,
    private val loggedInUserUuidSetter: (UUID) -> Unit
) {
    fun userUuid(userUuid: UUID) = loggedInUserUuidSetter(userUuid)
}

internal fun ApplicationTestBuilder.matchmakingsTestApp(): MatchmakingsTestApp {
    environment { config = ApplicationConfig("application-test.yaml") }
    createClient { install(ContentNegotiation) { json() } }
    val matchmakingRepository = MatchmakingInMemoryRepository()
    var jwtPrincipal: JWTPrincipal? = null
    application {
        installSerialization()
        val matchmakingModule =
            createMatchmakingModule(MockTransactionalRunner(), NopEventPublisher(), matchmakingRepository)
        val mockJwtConfig = MockJwtAuthenticationProvider.Config(jwtPrincipal)
        val jwtProvider = MockJwtAuthenticationProvider(mockJwtConfig)
        install(Authentication) { register(jwtProvider) }
        routing { installAppRouting(jwtProvider, matchmakingModule.matchmakingsController) }
    }
    val jwtPrincipalSetter = { userUuid: UUID ->
        jwtPrincipal = mockk<JWTPrincipal>().apply { every { subject } returns userUuid.toString() }
    }
    return MatchmakingsTestApp(matchmakingRepository, jwtPrincipalSetter)
}
