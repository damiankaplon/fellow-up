package io.fellowup.test.matchmaking

import io.fellowup.installAppRouting
import io.fellowup.installSerialization
import io.fellowup.matchmaking.MatchmakingRepository
import io.fellowup.matchmaking.infra.createMatchmakingModule
import io.fellowup.test.MockJwtAuthenticationProvider
import io.fellowup.test.MockTransactionalRunner
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import java.util.*

class MatchmakingsTestApp(
    val matchmakingRepository: MatchmakingRepository,
    private val loggedInUserUuidSetter: (UUID) -> Unit
) {
    fun userUuid(userUuid: UUID) = loggedInUserUuidSetter(userUuid)
}

fun ApplicationTestBuilder.matchmakingsTestApp(): MatchmakingsTestApp {
    environment { config = ApplicationConfig("application-test.yaml") }
    createClient { install(ContentNegotiation) { json() } }
    val matchmakingRepository = MatchmakingInMemoryRepository()
    var jwtPrincipal: JWTPrincipal? = null
    application {
        installSerialization()
        val matchmakingModule = createMatchmakingModule(MockTransactionalRunner(), matchmakingRepository)
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
