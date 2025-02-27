package io.fellowup.test.matchmaking

import io.fellowup.matchmaking.Matchmaking
import io.fellowup.matchmaking.MatchmakingRepository
import java.util.UUID

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
}
