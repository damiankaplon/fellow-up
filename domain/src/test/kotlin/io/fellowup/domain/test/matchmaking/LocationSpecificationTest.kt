package io.fellowup.domain.test.matchmaking

import io.fellowup.domain.matchmaking.Activity
import io.fellowup.domain.matchmaking.ActivityMatchSpecification.LocationSpecification
import io.fellowup.domain.matchmaking.DistanceCalculator
import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.test.fixtures.utcInstant
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

internal class LocationSpecificationTest {

    @Test
    fun `should match given activity within max distance`() {
        // given
        val distanceCalculator = object : DistanceCalculator {
            override fun calculateMeters(location1: Location, location2: Location): Double {
                return 2000.0
            }
        }
        val specification = LocationSpecification(distanceCalculator, maxDistanceInMeters = 2001)
        val matchmaking = Matchmaking(
            category = "SOCCER",
            userId = UUID.randomUUID(),
            at = "2025-02-23T16:59:00".utcInstant(),
            location = Location(0.0, 0.0)
        )
        val activity = Activity(
            category = "SOCCER",
            at = "2025-02-23T16:59:00".utcInstant(),
            location = Location(0.0, 0.0)
        )

        // when
        val isMatching = specification.isMatching(matchmaking, activity)

        // then
        assertThat(isMatching).isTrue()
    }

    @Test
    fun `should not match given activity out of the max distance`() {
        // given
        val distanceCalculator = object : DistanceCalculator {
            override fun calculateMeters(location1: Location, location2: Location): Double {
                return 2000.0
            }
        }
        val specification = LocationSpecification(distanceCalculator, maxDistanceInMeters = 1999)
        val matchmaking = Matchmaking(
            category = "SOCCER",
            userId = UUID.randomUUID(),
            at = "2025-02-23T16:59:00".utcInstant(),
            location = Location(0.0, 0.0)
        )
        val activity = Activity(
            category = "SOCCER",
            at = "2025-02-23T16:59:00".utcInstant(),
            location = Location(0.0, 0.0)
        )

        // when
        val isMatching = specification.isMatching(matchmaking, activity)

        // then
        assertThat(isMatching).isFalse()
    }
}
