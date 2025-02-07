package io.fellowup.test.matchmaking

import io.fellowup.installAppRouting
import io.fellowup.installSerialization
import io.fellowup.matchmaking.Matchmaking
import io.fellowup.matchmaking.MatchmakingsController
import io.fellowup.matchmaking.installMatchmakingModule
import io.fellowup.test.MockJwtAuthenticationProvider
import io.fellowup.test.NopTransactionalRunner
import io.fellowup.test.utcInstant
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

internal class MatchmakingCrudIntegrationTest {

    @Test
    fun `should create matchmaking`() = testApplication {
        // Given
        environment { config = ApplicationConfig("application-test.yaml") }
        val matchmakingRepository = MatchmakingInMemoryRepository()
        application {
            installSerialization()
            val matchmakingModule = installMatchmakingModule(NopTransactionalRunner(), matchmakingRepository)
            val jwtPrincipal: JWTPrincipal = mockk()
            every { jwtPrincipal.subject } returns UUID.randomUUID().toString()
            val mockJwtConfig = MockJwtAuthenticationProvider.Config(jwtPrincipal)
            val jwtProvider = MockJwtAuthenticationProvider(mockJwtConfig)
            install(Authentication) { register(jwtProvider) }
            routing { installAppRouting(jwtProvider, matchmakingModule.matchmakingsController) }
        }

        // When
        val response = createClient { install(ContentNegotiation) { json() } }.post("/api/matchmakings") {
            contentType(ContentType.Application.Json)
            setBody(
                MatchmakingsController.CreateMatchmakingBody(
                    category = "soccer",
                    at = Instant.parse("2021-01-01T00:00:00Z"),
                )
            )

        }
        // Then
        assertThat(response.status.value).isEqualTo(200)
        val body = response.body<MatchmakingsController.MatchmakingDto>()
        assertThat(body.id).isNotNull()
        assertThat(body.category).isEqualTo("soccer")
        assertThat(body.at).isEqualTo(Instant.parse("2021-01-01T00:00:00Z"))
    }

    @Test
    fun `should find all user matchmakings`() = testApplication {
        // Given
        var loggedInUserUuid = UUID.randomUUID()
        environment { config = ApplicationConfig("application-test.yaml") }
        val matchmakingRepository = MatchmakingInMemoryRepository()
        application {
            installSerialization()
            val matchmakingModule = installMatchmakingModule(NopTransactionalRunner(), matchmakingRepository)
            val jwtPrincipal: JWTPrincipal = mockk()
            every { jwtPrincipal.subject } returns loggedInUserUuid.toString()
            val mockJwtConfig = MockJwtAuthenticationProvider.Config(jwtPrincipal)
            val jwtProvider = MockJwtAuthenticationProvider(mockJwtConfig)
            install(Authentication) { register(jwtProvider) }
            routing { installAppRouting(jwtProvider, matchmakingModule.matchmakingsController) }
        }

        matchmakingRepository.save(
            Matchmaking(
                category = "SOCCER",
                userId = UUID.randomUUID(),
                at = "2025-02-07T16:59:00".utcInstant()
            )
        )
        matchmakingRepository.save(
            Matchmaking(
                category = "SOCCER",
                userId = loggedInUserUuid!!,
                at = "2025-02-07T17:00:00".utcInstant()
            )
        )

        // When
        val response = createClient { install(ContentNegotiation) { json() } }.get("/api/matchmakings")

        // Then
        assertThat(response.status.value).isEqualTo(200)
        val body = response.body<Set<MatchmakingsController.MatchmakingDto>>()
        assertThat(body).singleElement().satisfies(
            { matchmaking ->
                assertThat(matchmaking.id).isNotNull()
                assertThat(matchmaking.category).isEqualTo("SOCCER")
                assertThat(matchmaking.at).isEqualTo(Instant.parse("2025-02-07T17:00:00Z"))
            }
        )
    }
}