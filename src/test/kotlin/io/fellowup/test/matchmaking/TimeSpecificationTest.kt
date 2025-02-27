package io.fellowup.test.matchmaking

import io.fellowup.matchmaking.Activity
import io.fellowup.matchmaking.ActivityMatchSpecification.TimeSpecification
import io.fellowup.matchmaking.Location
import io.fellowup.matchmaking.Matchmaking
import io.fellowup.test.utcInstant
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.UUID

internal class TimeSpecificationTest {

    @Test
    fun `should return true when time difference is less than max time difference`() {
        // given
        val specification = TimeSpecification(maxTimeDifferenceInSeconds = 60)
        val matchmaking = Matchmaking(
            category = "SOCCER",
            userId = UUID.randomUUID(),
            at = "2025-02-23T16:59:00".utcInstant(),
            location = Location(0.0, 0.0)
        )
        val activity = Activity(
            category = "SOCCER",
            at = "2025-02-23T16:59:30".utcInstant(),
            location = Location(0.0, 0.0)
        )

        // when
        val isMatching = specification.isMatching(matchmaking, activity)

        // then
        assertThat(isMatching).isTrue()
    }

    @Test
    fun `should return false when time difference is more than max time difference`() {
        // given
        val specification = TimeSpecification(maxTimeDifferenceInSeconds = 360)
        val matchmaking = Matchmaking(
            category = "SOCCER",
            userId = UUID.randomUUID(),
            at = "2025-02-23T16:00:00".utcInstant(),
            location = Location(0.0, 0.0)
        )
        val activity = Activity(
            category = "SOCCER",
            at = "2025-02-23T17:01:00".utcInstant(),
            location = Location(0.0, 0.0)
        )

        // when
        val isMatching = specification.isMatching(matchmaking, activity)

        // then
        assertThat(isMatching).isFalse()
    }
}
