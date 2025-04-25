package io.fellowup.domain.test.fixtures.matchmaking

import haversineDistance
import io.fellowup.domain.matchmaking.Activity
import io.fellowup.domain.matchmaking.ActivityRepository
import io.fellowup.domain.matchmaking.Location
import java.time.Instant
import java.time.temporal.ChronoUnit

class ActivityInMemoryRepository : ActivityRepository {

    private val activities = mutableSetOf<Activity>()

    override suspend fun save(activity: Activity): Activity {
        activities.add(activity)
        return activity
    }

    override suspend fun findMatchingTo(
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxSecondsDiff: Int
    ): Set<Activity> {
        val minTime = time.minus(maxSecondsDiff.toLong(), ChronoUnit.SECONDS)
        val maxTime = time.plus(maxSecondsDiff.toLong(), ChronoUnit.SECONDS)

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
