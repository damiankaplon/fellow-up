import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.FloatColumnType

fun MetersBetween(
    p1Longitude: Expression<Double>,
    p1Latitude: Expression<Double>,
    p2Longitude: Expression<Double>,
    p2Latitude: Expression<Double>
) = CustomFunction(
    "metersBetweenTwoGeographicalPoints",
    FloatColumnType(),
    p1Longitude,
    p1Latitude,
    p2Longitude,
    p2Latitude
)
