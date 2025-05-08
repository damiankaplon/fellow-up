package io.fellowup.infrastructure.test.matchmaking

import io.fellowup.domain.matchmaking.Activity
import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.test.fixtures.utcInstant
import io.fellowup.infrastructure.matchmaking.infra.ActivityDaoRepository
import io.fellowup.infrastructure.test.DatabaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.dao.flushCache
import kotlin.test.Test

internal class ActivityRepositoryIntegrationTest : DatabaseIntegrationTest() {

    private val activityRepository = ActivityDaoRepository()

    @Test
    fun `should find activity within 10km and 2 hours`() = rollbackTransaction {
        // Given
        with(activityRepository) {
            save(
                Activity(
                    category = "SOCCER",
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187201, 16.188945)
                )
            )
            save(
                Activity(
                    category = "SOCCER",
                    at = "2025-02-14T18:00:00".utcInstant(),
                    location = Location(56.096901, 16.188945)
                )
            )
        }
        flushCache()

        // When
        val result: Set<Activity> = activityRepository.findMatchingTo(
            location = Location(54.187567, 16.190301),
            maxMetersDiff = 10_000,
            time = "2025-02-14T17:00:00".utcInstant(),
            maxSecondsDiff = 7200
        )

        // Then
        assertThat(result).singleElement().satisfies(
            { assertThat(it.category).isEqualTo("SOCCER") },
            { assertThat(it.at).isEqualTo("2025-02-14T16:00:00".utcInstant()) },
            { assertThat(it.location).isEqualTo(Location(54.187201, 16.188945)) }
        )
    }

    @Test
    fun `should not find activities out of range`() = rollbackTransaction {
        // Given
        with(activityRepository) {
            save(
                Activity(
                    category = "SOCCER",
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187201, 16.188945)
                )
            )
            save(
                Activity(
                    category = "SOCCER",
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187152, 16.189607)
                )
            )
        }
        flushCache()

        // When
        val result: Set<Activity> = activityRepository.findMatchingTo(
            location = Location(54.183267, 16.194264),
            maxMetersDiff = 1,
            time = "2025-02-14T16:00:00".utcInstant(),
            maxSecondsDiff = 60
        )

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should not find activities out of time`() = rollbackTransaction {
        // Given
        with(activityRepository) {
            save(
                Activity(
                    category = "SOCCER",
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187201, 16.188945)
                )
            )
            save(
                Activity(
                    category = "SOCCER",
                    at = "2025-02-14T18:00:00".utcInstant(),
                    location = Location(54.187152, 16.189607)
                )
            )
        }
        flushCache()

        // When
        val result: Set<Activity> = activityRepository.findMatchingTo(
            location = Location(54.187567, 16.190301),
            maxMetersDiff = 10_000,
            time = "2025-02-14T17:00:00".utcInstant(),
            maxSecondsDiff = 59
        )

        // Then
        assertThat(result).isEmpty()
    }
}
