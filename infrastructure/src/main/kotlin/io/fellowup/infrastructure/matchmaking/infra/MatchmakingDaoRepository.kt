package io.fellowup.infrastructure.matchmaking.infra

import MetersBetween
import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.matchmaking.MatchmakingRepository
import io.fellowup.infrastructure.matchmaking.infra.MatchmakingDao.MatchmakingsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.doubleParam
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class MatchmakingDaoRepository : MatchmakingRepository {

    override suspend fun save(matchmaking: Matchmaking): Matchmaking {
        val entity = MatchmakingDao.findByIdAndUpdate(matchmaking.id.value) { it.from(matchmaking) }
            ?: MatchmakingDao.new(id = matchmaking.id.value) { this.from(matchmaking) }
        return entity.toMatchmaking()
    }

    override suspend fun findAllByUserId(usedId: UUID): Set<Matchmaking> {
        return MatchmakingDao.find { MatchmakingsTable.user_id eq usedId }
            .map { it.toMatchmaking() }.toSet()
    }

    override suspend fun findById(id: Matchmaking.Id): Matchmaking? {
        return MatchmakingDao.findById(id.value)?.toMatchmaking()
    }

    override suspend fun findMatchingTo(
        category: String,
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxSecondsDiff: Int
    ): Set<Matchmaking> = MatchmakingDao.find {
        MatchmakingsTable.category eq category and MatchmakingsTable.at.between(
            time.minus(maxSecondsDiff.toLong(), ChronoUnit.SECONDS),
            time.plus(maxSecondsDiff.toLong(), ChronoUnit.SECONDS)
        ) and MetersBetween(
            MatchmakingsTable.longitude,
            MatchmakingsTable.latitude,
            doubleParam(location.longitude),
            doubleParam(location.latitude)
        ).lessEq(maxMetersDiff.toFloat())
    }.mapTo(linkedSetOf()) { it.toMatchmaking() }

    private fun MatchmakingDao.from(matchmaking: Matchmaking) {
        this.category = matchmaking.category
        this.at = matchmaking.at
        this.userId = matchmaking.userId
        this.latitude = matchmaking.location.latitude
        this.longitude = matchmaking.location.longitude
    }

    private fun MatchmakingDao.toMatchmaking() = Matchmaking(
        id = Matchmaking.Id(id.value),
        category = category,
        userId = userId,
        at = at,
        location = Location(longitude, latitude)
    )
}
