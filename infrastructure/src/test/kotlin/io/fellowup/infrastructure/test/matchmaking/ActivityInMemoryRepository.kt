package io.fellowup.infrastructure.test.matchmaking

import io.fellowup.domain.matchmaking.Activity
import io.fellowup.domain.matchmaking.ActivityRepository
import io.fellowup.domain.matchmaking.Location
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // meters

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}