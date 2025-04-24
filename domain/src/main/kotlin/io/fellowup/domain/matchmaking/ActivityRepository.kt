package io.fellowup.domain.matchmaking

import java.time.Instant

interface ActivityRepository {

    suspend fun save(activity: Activity): Activity

    suspend fun findMatchingTo(
        location: Location,
        maxMetersDiff: Int,
        time: Instant,
        maxMinutesDiff: Int
    ): Set<Activity>
}
