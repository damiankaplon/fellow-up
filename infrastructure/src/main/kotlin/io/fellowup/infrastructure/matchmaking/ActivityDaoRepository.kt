package io.fellowup.infrastructure.matchmaking

import MetersBetween
import io.fellowup.domain.matchmaking.Activity
import io.fellowup.domain.matchmaking.ActivityRepository
import io.fellowup.domain.matchmaking.Location
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.doubleParam
import java.time.Instant
import java.time.temporal.ChronoUnit

class ActivityDaoRepository : ActivityRepository {

    override suspend fun save(activity: Activity): Activity {
        val entity = ActivityDao.findByIdAndUpdate(activity.id.value) { it.from(activity) }
            ?: ActivityDao.new(id = activity.id.value) { this.from(activity) }
        return entity.toActivity()
    }

    override suspend fun findMatchingTo(
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxSecondsDiff: Int
    ): Set<Activity> = ActivityDao.find {
        ActivityDao.ActivitiesTable.at.between(
            time.minus(maxSecondsDiff.toLong(), ChronoUnit.SECONDS),
            time.plus(maxSecondsDiff.toLong(), ChronoUnit.SECONDS)
        ) and MetersBetween(
            ActivityDao.ActivitiesTable.longitude,
            ActivityDao.ActivitiesTable.latitude,
            doubleParam(location.longitude),
            doubleParam(location.latitude)
        ).lessEq(maxMetersDiff.toFloat())
    }.mapTo(linkedSetOf()) { it.toActivity() }

    private fun ActivityDao.from(activity: Activity) {
        this.category = activity.category
        this.at = activity.at
        this.longitude = activity.location.longitude
        this.latitude = activity.location.latitude
    }
}