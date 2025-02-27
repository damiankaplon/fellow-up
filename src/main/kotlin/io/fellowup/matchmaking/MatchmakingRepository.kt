package io.fellowup.matchmaking

import java.util.*

interface MatchmakingRepository {
    suspend fun save(matchmaking: Matchmaking): Matchmaking
    suspend fun findAllByUserId(usedId: UUID): Set<Matchmaking>
    suspend fun findById(id: Matchmaking.Id): Matchmaking?
    suspend fun findByIdOrThrow(id: Matchmaking.Id): Matchmaking =
        findById(id) ?: error("Matchmaking id: $id not found")
}
