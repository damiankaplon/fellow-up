package io.fellowup.test.matchmaking

import io.fellowup.matchmaking.Location
import io.fellowup.matchmaking.infra.PlainKotlinDistanceCalculator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class PlainKotlinDistanceCalculatorTest {

    private val calculator = PlainKotlinDistanceCalculator()


    @Test
    fun `should calculate proper distance between two coordinates`() {
        // Given
        val location1 = Location(longitude = 54.160918, latitude = 16.178024)
        val location2 = Location(longitude = 54.165900, latitude = 16.184698)

        // When
        val distance: Double = calculator.calculateMeters(location1, location2)

        // Then
        assertThat(distance).isEqualTo(913.1196338996297)
    }
}
