package io.fellowup.matchmaking.infra

import io.fellowup.matchmaking.Location
import io.fellowup.matchmaking.Matchmaking
import io.fellowup.matchmaking.MatchmakingRepository
import io.fellowup.matchmaking.infra.MatchmakingDao.MatchmakingsTable
import java.util.*

class MatchmakingDaoRepository : MatchmakingRepository {

    override fun save(matchmaking: Matchmaking): Matchmaking {
        val entity = MatchmakingDao.findByIdAndUpdate(matchmaking.id.value) { it.from(matchmaking) }
            ?: MatchmakingDao.new { this.from(matchmaking) }
        return entity.toDomain()
    }

    override fun findAllByUserId(usedId: UUID): Set<Matchmaking> {
        return MatchmakingDao.find { MatchmakingsTable.user_id eq usedId }
            .map { it.toDomain() }.toSet()
    }

    private fun MatchmakingDao.from(matchmaking: Matchmaking) {
        this.category = matchmaking.category
        this.at = matchmaking.at
        this.userId = matchmaking.userId
        this.latitude = matchmaking.location.latitude
        this.longitude = matchmaking.location.longitude
    }

    private fun MatchmakingDao.toDomain() = Matchmaking(
        id = Matchmaking.Id(id.value),
        category = category,
        userId = userId,
        at = at,
        location = Location(latitude, longitude)
    )
}