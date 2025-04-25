package io.fellowup.domain.test.mediation

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.mediation.Mediation
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import java.time.Instant.parse
import java.util.*
import kotlin.test.Test

internal class MediationCreatingTest {

    @Test
    fun `should not allow to create a mediation out of different category matchmakings`() {
        // given
        val matchmaking1 = Matchmaking(
            category = "soccer",
            userId = UUID.randomUUID(),
            at = parse("2025-04-14T11:00:00Z"),
            location = Location(51.685713, -4.206430)
        )

        val matchmaking2 = Matchmaking(
            category = "volleyball",
            userId = UUID.randomUUID(),
            at = parse("2025-04-14T11:05:00Z"),
            location = Location(51.685800, -4.206500)
        )

        val matchmaking3 = Matchmaking(
            category = "soccer",
            userId = UUID.randomUUID(),
            at = parse("2025-04-14T11:10:00Z"),
            location = Location(51.685900, -4.206700)
        )

        // when
        val result = catchException { Mediation(setOf(matchmaking1, matchmaking2, matchmaking3)) }

        // then
        assertThat(result).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should not allow to create a mediation out of matchmakings belonging to the same user`() {
        // given
        val userId = UUID.randomUUID()
        val matchmaking1 = Matchmaking(
            category = "soccer",
            userId = userId,
            at = parse("2025-04-14T11:00:00Z"),
            location = Location(51.685713, -4.206430)
        )

        val matchmaking2 = Matchmaking(
            category = "volleyball",
            userId = userId,
            at = parse("2025-04-14T11:05:00Z"),
            location = Location(51.685800, -4.206500)
        )

        val matchmaking3 = Matchmaking(
            category = "soccer",
            userId = userId,
            at = parse("2025-04-14T11:10:00Z"),
            location = Location(51.685900, -4.206700)
        )

        // when
        val result = catchException { Mediation(setOf(matchmaking1, matchmaking2, matchmaking3)) }

        // then
        assertThat(result).isInstanceOf(IllegalArgumentException::class.java)
    }
}
