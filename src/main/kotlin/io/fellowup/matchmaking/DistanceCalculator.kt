package io.fellowup.matchmaking

interface DistanceCalculator {
    fun calculateMeters(location1: Location, location2: Location): Double
}
