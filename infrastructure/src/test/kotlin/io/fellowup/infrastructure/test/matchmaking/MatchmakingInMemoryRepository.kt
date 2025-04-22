package io.fellowup.infrastructure.test.matchmaking

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.matchmaking.MatchmakingRepository
import java.time.Instant
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

    override suspend fun findDistanceWithinAndTimeDiffWithinAndCategory(
        category: String,
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxMinutesDiff: Int
    ): Set<Matchmaking> {
        return emptySet()
    }
}
