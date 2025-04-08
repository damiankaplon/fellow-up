package io.fellowup.infrastructure.matchmaking.infra

import io.fellowup.domain.matchmaking.DistanceCalculator
import io.fellowup.domain.matchmaking.Location
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PlainKotlinDistanceCalculator : DistanceCalculator {
    override fun calculateMeters(
        location1: Location,
        location2: Location
    ): Double {
        val earthRadius = 6371000.0

        val dLat = Math.toRadians(location2.latitude - location1.latitude)
        val dLon = Math.toRadians(location2.longitude - location1.longitude)

        val lat1 = Math.toRadians(location1.latitude)
        val lat2 = Math.toRadians(location2.latitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}
