package io.fellowup.infrastructure.test.matchmaking

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.test.fixtures.utcInstant
import io.fellowup.infrastructure.matchmaking.MatchmakingDaoRepository
import io.fellowup.infrastructure.test.DatabaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.dao.flushCache
import java.util.*
import kotlin.test.Test

internal class MatchmakingRepositoryIntegrationTest : DatabaseIntegrationTest() {

    private val matchmakingRepository = MatchmakingDaoRepository()

    private val userId1 = UUID.randomUUID()
    private val userId2 = UUID.randomUUID()

    @Test
    fun `should find matchmaking within 10km and 2 hours`() = rollbackTransaction {
        // Given
        with(matchmakingRepository) {
            save(
                Matchmaking(
                    id = Matchmaking.Id(UUID.randomUUID()),
                    category = "SOCCER",
                    userId = userId1,
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187201, 16.188945)
                )
            )
            save(
                Matchmaking(
                    id = Matchmaking.Id(UUID.randomUUID()),
                    category = "SOCCER",
                    userId = userId2,
                    at = "2025-02-14T18:00:00".utcInstant(),
                    location = Location(56.096901, 16.188945)
                )
            )
        }
        flushCache()

        // When
        val result: Set<Matchmaking> = matchmakingRepository.findMatchingTo(
            category = "SOCCER",
            location = Location(54.187567, 16.190301),
            maxMetersDiff = 10_000,
            time = "2025-02-14T17:00:00".utcInstant(),
            maxSecondsDiff = 7200
        )

        // Then
        assertThat(result).singleElement().satisfies(
            { assertThat(it.category).isEqualTo("SOCCER") },
            { assertThat(it.userId).isEqualTo(userId1) },
            { assertThat(it.at).isEqualTo("2025-02-14T16:00:00".utcInstant()) },
            { assertThat(it.location).isEqualTo(Location(54.187201, 16.188945)) }
        )
    }

    @Test
    fun `should not find matchmakings out of range`() = rollbackTransaction {
        // Given
        with(matchmakingRepository) {
            save(
                Matchmaking(
                    id = Matchmaking.Id(UUID.randomUUID()),
                    category = "SOCCER",
                    userId = userId1,
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187201, 16.188945)
                )
            )
            save(
                Matchmaking(
                    id = Matchmaking.Id(UUID.randomUUID()),
                    category = "SOCCER",
                    userId = userId2,
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187152, 16.189607)
                )
            )
        }
        flushCache()

        // When
        val result: Set<Matchmaking> = matchmakingRepository.findMatchingTo(
            category = "SOCCER",
            location = Location(54.183267, 16.194264),
            maxMetersDiff = 1,
            time = "2025-02-14T16:00:00".utcInstant(),
            maxSecondsDiff = 60
        )

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should not find matchmakings out of time`() = rollbackTransaction {
        // Given
        with(matchmakingRepository) {
            save(
                Matchmaking(
                    id = Matchmaking.Id(UUID.randomUUID()),
                    category = "SOCCER",
                    userId = userId1,
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187201, 16.188945)
                )
            )
            save(
                Matchmaking(
                    id = Matchmaking.Id(UUID.randomUUID()),
                    category = "SOCCER",
                    userId = userId2,
                    at = "2025-02-14T18:00:00".utcInstant(),
                    location = Location(54.187152, 16.189607)
                )
            )
        }
        flushCache()

        // When
        val result: Set<Matchmaking> = matchmakingRepository.findMatchingTo(
            category = "SOCCER",
            location = Location(54.187567, 16.190301),
            maxMetersDiff = 10_000,
            time = "2025-02-14T17:00:00".utcInstant(),
            maxSecondsDiff = 59
        )

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should not find matchmakings with different category`() = rollbackTransaction {
        // Given
        with(matchmakingRepository) {
            save(
                Matchmaking(
                    id = Matchmaking.Id(UUID.randomUUID()),
                    category = "SOCCER",
                    userId = userId1,
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187201, 16.188945)
                )
            )
            save(
                Matchmaking(
                    id = Matchmaking.Id(UUID.randomUUID()),
                    category = "BASKETBALL",
                    userId = userId2,
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187152, 16.189607)
                )
            )
        }
        flushCache()

        // When
        val result: Set<Matchmaking> = matchmakingRepository.findMatchingTo(
            category = "TENNIS",
            location = Location(54.187567, 16.190301),
            maxMetersDiff = 10_000,
            time = "2025-02-14T16:00:00".utcInstant(),
            maxSecondsDiff = 60
        )

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should find all matchmakings by user id`() = rollbackTransaction {
        // Given
        val matchmaking1 = Matchmaking(
            id = Matchmaking.Id(UUID.randomUUID()),
            category = "SOCCER",
            userId = userId1,
            at = "2025-02-14T16:00:00".utcInstant(),
            location = Location(54.187201, 16.188945)
        )
        val matchmaking2 = Matchmaking(
            id = Matchmaking.Id(UUID.randomUUID()),
            category = "BASKETBALL",
            userId = userId1,
            at = "2025-02-15T16:00:00".utcInstant(),
            location = Location(54.187152, 16.189607)
        )
        val matchmaking3 = Matchmaking(
            id = Matchmaking.Id(UUID.randomUUID()),
            category = "TENNIS",
            userId = userId2,
            at = "2025-02-16T16:00:00".utcInstant(),
            location = Location(54.183267, 16.194264)
        )

        with(matchmakingRepository) {
            save(matchmaking1)
            save(matchmaking2)
            save(matchmaking3)
        }
        flushCache()

        // When
        val result: Set<Matchmaking> = matchmakingRepository.findAllByUserId(userId1)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result).satisfiesExactlyInAnyOrder(
            { assertThat(it.id).isEqualTo(matchmaking1.id) },
            { assertThat(it.id).isEqualTo(matchmaking2.id) }
        )
    }

    @Test
    fun `should find matchmaking by id`() = rollbackTransaction {
        // Given
        val matchmakingId = UUID.randomUUID()
        val matchmaking = Matchmaking(
            id = Matchmaking.Id(matchmakingId),
            category = "SOCCER",
            userId = userId1,
            at = "2025-02-14T16:00:00".utcInstant(),
            location = Location(54.187201, 16.188945)
        )

        with(matchmakingRepository) {
            save(matchmaking)
            save(
                Matchmaking(
                    id = Matchmaking.Id(UUID.randomUUID()),
                    category = "BASKETBALL",
                    userId = userId2,
                    at = "2025-02-15T16:00:00".utcInstant(),
                    location = Location(54.187152, 16.189607)
                )
            )
        }
        flushCache()

        // When
        val result: Matchmaking? = matchmakingRepository.findById(Matchmaking.Id(matchmakingId))

        // Then
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(matchmaking)
    }

    @Test
    fun `should return null when matchmaking not found by id`() = rollbackTransaction {
        // Given
        val nonExistentId = UUID.randomUUID()

        with(matchmakingRepository) {
            save(
                Matchmaking(
                    id = Matchmaking.Id(UUID.randomUUID()),
                    category = "SOCCER",
                    userId = userId1,
                    at = "2025-02-14T16:00:00".utcInstant(),
                    location = Location(54.187201, 16.188945)
                )
            )
        }
        flushCache()

        // When
        val result: Matchmaking? = matchmakingRepository.findById(Matchmaking.Id(nonExistentId))

        // Then
        assertThat(result).isNull()
    }
}
