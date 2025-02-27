package io.fellowup.matchmaking

import kotlin.math.abs

sealed interface ActivityMatchSpecification {

    fun isMatching(matchmaking: Matchmaking, activity: Activity): Boolean

    class LocationSpecification(
        private val distanceCalculator: DistanceCalculator,
        private val maxDistanceInMeters: Int
    ) : ActivityMatchSpecification {

        override fun isMatching(matchmaking: Matchmaking, activity: Activity): Boolean {
            val distance = distanceCalculator.calculateMeters(matchmaking.location, activity.location)
            return distance <= maxDistanceInMeters
        }
    }

    class TimeSpecification(
        private val maxTimeDifferenceInSeconds: Int
    ) : ActivityMatchSpecification {

        override fun isMatching(matchmaking: Matchmaking, activity: Activity): Boolean {
            val secondsDiff = abs(matchmaking.at.epochSecond - activity.at.epochSecond)

            return secondsDiff <= maxTimeDifferenceInSeconds
        }
    }
}
