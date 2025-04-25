package io.fellowup.infrastructure.test.matchmaking

import haversineDistance
import io.fellowup.domain.matchmaking.Activity
import io.fellowup.domain.matchmaking.ActivityRepository
import io.fellowup.domain.matchmaking.Location
import java.time.Instant
import java.time.temporal.ChronoUnit

internal class ActivityInMemoryRepository : ActivityRepository {

    private val activities = mutableSetOf<Activity>()

    override suspend fun save(activity: Activity): Activity {
        activities.add(activity)
        return activity
    }

    override suspend fun findMatchingTo(
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxMinutesDiff: Int
    ): Set<Activity> {
        val minTime = time.minus(maxMinutesDiff.toLong(), ChronoUnit.MINUTES)
        val maxTime = time.plus(maxMinutesDiff.toLong(), ChronoUnit.MINUTES)

        return activities
            .filter {
                it.at.isAfter(minTime) && it.at.isBefore(maxTime)
            }
            .filter {
                haversineDistance(
                    it.location.latitude,
                    it.location.longitude,
                    location.latitude,
                    location.longitude
                ) <= maxMetersDiff
            }
            .toSet()
    }
}
