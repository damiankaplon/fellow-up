package io.fellowup.infrastructure.matchmaking.infra

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.matchmaking.MatchmakingRepository
import io.fellowup.infrastructure.matchmaking.infra.ActivityDao.ActivitiesTable
import io.fellowup.infrastructure.matchmaking.infra.MatchmakingDao.MatchmakingsTable
import io.fellowup.java.toUUID
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.withSuspendTransaction
import java.sql.ResultSet
import java.time.Instant
import java.util.*

class MatchmakingDaoRepository : MatchmakingRepository {

    override suspend fun save(matchmaking: Matchmaking): Matchmaking {
        val entity = MatchmakingDao.findByIdAndUpdate(matchmaking.id.value) { it.from(matchmaking) }
            ?: MatchmakingDao.new { this.from(matchmaking) }
        return entity.toMatchmaking()
    }

    override suspend fun findAllByUserId(usedId: UUID): Set<Matchmaking> {
        return MatchmakingDao.find { MatchmakingsTable.user_id eq usedId }
            .map { it.toMatchmaking() }.toSet()
    }

    override suspend fun findById(id: Matchmaking.Id): Matchmaking? {
        return MatchmakingDao.findById(id.value)?.toMatchmaking()
    }

    override suspend fun findDistanceWithinAndTimeDiffWithinAndCategory(
        category: String,
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxMinutesDiff: Int
    ): Set<Matchmaking> = TransactionManager.current().withSuspendTransaction {
        val query = """
                SELECT id 
                FROM matchmaking 
                WHERE at BETWEEN 
                            (TO_TIMESTAMP(?) - make_interval(0, 0, 0, 0, 0, ?, 0)) 
                                AND 
                            (TO_TIMESTAMP(?) + make_interval(0, 0, 0, 0, 0, ?, 0)) 
                    AND (ST_DISTANCE(
                            st_setsrid(ST_MakePoint(longitude, latitude), 4326)::geography,
                            st_setsrid(ST_MakePoint(?, ?), 4326)::geography
                        ) <= ?
                    )
                    AND category = ?;
                """.trimIndent()
        val similarMatchmakingIds: List<UUID> = exec(
            stmt = query, args = listOf(
                IntegerColumnType() to time.epochSecond,
                IntegerColumnType() to maxMinutesDiff,
                IntegerColumnType() to time.epochSecond,
                IntegerColumnType() to maxMinutesDiff,
                ActivitiesTable.longitude.columnType to location.longitude,
                ActivitiesTable.latitude.columnType to location.latitude,
                IntegerColumnType() to maxMetersDiff,
                TextColumnType() to category
            )
        ) { resultSet: ResultSet? ->
            val result = mutableListOf<UUID>()
            while (resultSet != null && resultSet.next()) {
                result.add(resultSet.getString(1).toUUID())
            }
            result
        }?.toList() ?: emptyList()
        return@withSuspendTransaction MatchmakingDao.find { (ActivitiesTable.id inList similarMatchmakingIds) }
            .map { it.toMatchmaking() }
            .toSet()
    }

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
