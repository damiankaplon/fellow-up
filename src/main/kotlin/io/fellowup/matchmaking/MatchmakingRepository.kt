package io.fellowup.matchmaking

import java.util.*

interface MatchmakingRepository {
    fun save(matchmaking: Matchmaking): Matchmaking
    fun findAllByUserId(usedId: UUID): Set<Matchmaking>
}