package io.fellowup.infrastructure.test.matchmaking

import haversineDistance
import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.matchmaking.MatchmakingRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

internal class MatchmakingInMemoryRepository : MatchmakingRepository {

    private val matchmakings = mutableSetOf<Matchmaking>()

    override suspend fun save(matchmaking: Matchmaking): Matchmaking {
        matchmakings.add(matchmaking)
        return matchmaking
    }

    override suspend fun findAllByUserId(usedId: UUID): Set<Matchmaking> {
        return matchmakings.filter { it.userId == usedId }.toSet()
    }

    override suspend fun findById(id: Matchmaking.Id): Matchmaking? {
        return matchmakings.find { it.id == id }
    }

    override suspend fun findMatchingTo(
        category: String,
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxMinutesDiff: Int
    ): Set<Matchmaking> {
        return matchmakings.filter { it.category == category }
            .filter {
                it.at.isAfter(time.minus(maxMinutesDiff.toLong(), ChronoUnit.MINUTES)) ||
                        it.at.isBefore(time.plus(maxMinutesDiff.toLong(), ChronoUnit.MINUTES))
            }
            .filter {
                haversineDistance(
                    it.location.latitude,
                    it.location.longitude,
                    location.latitude,
                    location.longitude
                ) < maxMetersDiff
            }
            .toSet()
    }
}
