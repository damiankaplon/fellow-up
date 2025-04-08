package io.fellowup.domain.matchmaking

interface DistanceCalculator {
    fun calculateMeters(location1: Location, location2: Location): Double
}
