package io.fellowup.infrastructure.test.matchmaking

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.test.fixtures.utcInstant
import io.fellowup.infrastructure.matchmaking.infra.MatchmakingController
import io.fellowup.infrastructure.test.clientJson
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

internal class MatchmakingCrudKtorIntegrationTest {

    @Test
    fun `should create matchmaking`() = testApplication {
        // Given
        val testApp = matchmakingsTestApp()
        testApp.userUuid(UUID.randomUUID())

        // When
        val response = clientJson.post("/api/matchmakings") {
            contentType(ContentType.Application.Json)
            setBody(
                MatchmakingController.CreateMatchmakingBody(
                    category = "soccer",
                    at = Instant.parse("2021-01-01T00:00:00Z"),
                    location = MatchmakingController.LocationDto(
                        lat = 0.0,
                        lng = 0.0
                    )
                )
            )

        }
        // Then
        assertThat(response.status.value).isEqualTo(200)
        val body = response.body<MatchmakingController.MatchmakingDto>()
        assertThat(body.id).isNotNull()
        assertThat(body.category).isEqualTo("soccer")
        assertThat(body.at).isEqualTo(Instant.parse("2021-01-01T00:00:00Z"))
    }

    @Test
    fun `should find all user matchmakings`() = testApplication {
        // Given
        val matchmakingsTestApp = matchmakingsTestApp()
        val matchmakingRepository = matchmakingsTestApp.matchmakingRepository

        matchmakingRepository.save(
            Matchmaking(
                category = "SOCCER",
                userId = UUID.randomUUID(),
                at = "2025-02-07T16:59:00".utcInstant(),
                location = Location(0.0, 0.0)
            )
        )
        val loggedInUserUuid = UUID.randomUUID()
        matchmakingsTestApp.userUuid(loggedInUserUuid)
        matchmakingRepository.save(
            Matchmaking(
                category = "SOCCER",
                userId = loggedInUserUuid!!,
                at = "2025-02-07T17:00:00".utcInstant(),
                location = Location(0.0, 0.0)
            )
        )

        // When
        val response = clientJson.get("/api/matchmakings")

        // Then
        assertThat(response.status.value).isEqualTo(200)
        val body = response.body<Set<MatchmakingController.MatchmakingDto>>()
        assertThat(body).singleElement().satisfies(
            { matchmaking ->
                assertThat(matchmaking.id).isNotNull()
                assertThat(matchmaking.category).isEqualTo("SOCCER")
                assertThat(matchmaking.at).isEqualTo(Instant.parse("2025-02-07T17:00:00Z"))
            }
        )
    }
}
