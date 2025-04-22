package io.fellowup.infrastructure.matchmaking.infra

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

internal class MatchmakingDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MatchmakingDao>(MatchmakingsTable)

    var category: String by MatchmakingsTable.category
    var userId: UUID by MatchmakingsTable.user_id
    var at: Instant by MatchmakingsTable.at
    var latitude: Double by MatchmakingsTable.latitude
    var longitude: Double by MatchmakingsTable.longitude

    object MatchmakingsTable : UUIDTable("matchmaking") {
        val category = text("category")
        val user_id = uuid("user_id")
        val at = timestamp("at")
        val latitude = double("latitude")
        val longitude = double("longitude")
    }
}
