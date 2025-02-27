package io.fellowup.matchmaking

import java.time.Instant

interface ActivityRepository {
    suspend fun save(activity: Activity): Activity
    suspend fun findDistanceWithinAndTimeDiffWithin(
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxMinutesDiff: Int
    ): Set<Activity>
}
