package io.fellowup.infrastructure.matchmaking.infra

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.matchmaking.MatchmakingRepository
import io.fellowup.matchmaking.infra.MatchmakingDao
import io.fellowup.matchmaking.infra.MatchmakingDao.MatchmakingsTable
import java.util.*

class MatchmakingDaoRepository : MatchmakingRepository {

    override suspend fun save(matchmaking: Matchmaking): Matchmaking {
        val entity = MatchmakingDao.findByIdAndUpdate(matchmaking.id.value) { it.from(matchmaking) }
            ?: MatchmakingDao.new { this.from(matchmaking) }
        return entity.toDomain()
    }

    override suspend fun findAllByUserId(usedId: UUID): Set<Matchmaking> {
        return MatchmakingDao.find { MatchmakingsTable.user_id eq usedId }
            .map { it.toDomain() }.toSet()
    }

    override suspend fun findById(id: Matchmaking.Id): Matchmaking? {
        return MatchmakingDao.findById(id.value)?.toDomain()
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
        location = Location(longitude, latitude)
    )
}
