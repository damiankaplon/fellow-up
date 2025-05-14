package io.fellowup.infrastructure.matchmaking

import io.fellowup.domain.matchmaking.Activity
import io.fellowup.domain.matchmaking.Location
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

internal class ActivityDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ActivityDao>(ActivitiesTable)

    var category: String by ActivitiesTable.category
    var at: Instant by ActivitiesTable.at
    var latitude: Double by ActivitiesTable.latitude
    var longitude: Double by ActivitiesTable.longitude

    fun toActivity() = Activity(
        id = Activity.Id(id.value),
        category = category,
        at = at,
        location = Location(longitude, latitude)
    )

    object ActivitiesTable : UUIDTable("activity") {
        val category = text("category")
        val at = timestamp("at")
        val latitude = double("latitude")
        val longitude = double("longitude")
    }
}