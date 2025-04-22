package io.fellowup.domain.matchmaking

import java.time.Instant
import java.util.*

interface MatchmakingRepository {
    suspend fun save(matchmaking: Matchmaking): Matchmaking
    suspend fun findAllByUserId(usedId: UUID): Set<Matchmaking>
    suspend fun findById(id: Matchmaking.Id): Matchmaking?
    suspend fun findByIdOrThrow(id: Matchmaking.Id): Matchmaking =
        findById(id) ?: error("Matchmaking id: $id not found")

    // TODO it'd be better to introduce common domain specification pattern interface and exposed implementation
    suspend fun findDistanceWithinAndTimeDiffWithinAndCategory(
        category: String,
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxMinutesDiff: Int
    ): Set<Matchmaking>
}
