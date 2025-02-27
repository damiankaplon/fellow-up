package io.fellowup.matchmaking.infra

import io.fellowup.java.toUUID
import io.fellowup.matchmaking.Activity
import io.fellowup.matchmaking.ActivityRepository
import io.fellowup.matchmaking.Location
import io.fellowup.matchmaking.infra.ActivityDao.ActivitiesTable
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.withSuspendTransaction
import java.sql.ResultSet
import java.time.Instant
import java.util.*

class ActivityDaoRepository : ActivityRepository {

    override suspend fun save(activity: Activity): Activity {
        val entity = ActivityDao.findByIdAndUpdate(activity.id.value) { it.from(activity) }
            ?: ActivityDao.new { this.from(activity) }
        return entity.toActivity()
    }

    override suspend fun findDistanceWithinAndTimeDiffWithin(
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxMinutesDiff: Int
    ): Set<Activity> = TransactionManager.current().withSuspendTransaction {
        val query = """
                SELECT id 
                FROM activity 
                WHERE at BETWEEN 
                            (TO_TIMESTAMP(?) - make_interval(0, 0, 0, 0, 0, ?, 0)) 
                                AND 
                            (TO_TIMESTAMP(?) + make_interval(0, 0, 0, 0, 0, ?, 0)) 
                    AND (ST_DISTANCE(
                            st_setsrid(ST_MakePoint(longitude, latitude), 4326)::geography,
                            st_setsrid(ST_MakePoint(?, ?), 4326)::geography
                        ) <= ?
                    );
                """.trimIndent()
        val matchingActivityIds: List<UUID> = exec(
            stmt = query, args = listOf(
                IntegerColumnType() to time.epochSecond,
                IntegerColumnType() to maxMinutesDiff,
                IntegerColumnType() to time.epochSecond,
                IntegerColumnType() to maxMinutesDiff,
                ActivitiesTable.longitude.columnType to location.longitude,
                ActivitiesTable.latitude.columnType to location.latitude,
                IntegerColumnType() to maxMetersDiff
            )
        ) { resultSet: ResultSet? ->
            val result = mutableListOf<UUID>()
            while (resultSet != null && resultSet.next()) {
                result.add(resultSet.getString(1).toUUID())
            }
            result
        }?.toList() ?: emptyList()
        return@withSuspendTransaction ActivityDao.find { (ActivitiesTable.id inList matchingActivityIds) }
            .map { it.toActivity() }
            .toSet()
    }

    private fun ActivityDao.from(activity: Activity) {
        this.category = activity.category
        this.at = activity.at
        this.longitude = activity.location.longitude
        this.latitude = activity.location.latitude
    }
}
