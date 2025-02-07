package io.fellowup.test.matchmaking

import io.fellowup.matchmaking.Matchmaking
import io.fellowup.matchmaking.MatchmakingsController
import io.fellowup.test.clientJson
import io.fellowup.test.utcInstant
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

internal class MatchmakingCrudTest {

    @Test
    fun `should create matchmaking`() = testApplication {
        // Given
        val testApp = matchmakingsTestApp()
        testApp.userUuid(UUID.randomUUID())

        // When
        val response = clientJson.post("/api/matchmakings") {
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
        val matchmakingsTestApp = matchmakingsTestApp()
        val matchmakingRepository = matchmakingsTestApp.matchmakingRepository

        matchmakingRepository.save(
            Matchmaking(
                category = "SOCCER",
                userId = UUID.randomUUID(),
                at = "2025-02-07T16:59:00".utcInstant()
            )
        )
        val loggedInUserUuid = UUID.randomUUID()
        matchmakingsTestApp.userUuid(loggedInUserUuid)
        matchmakingRepository.save(
            Matchmaking(
                category = "SOCCER",
                userId = loggedInUserUuid!!,
                at = "2025-02-07T17:00:00".utcInstant()
            )
        )

        // When
        val response = clientJson.get("/api/matchmakings")

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
