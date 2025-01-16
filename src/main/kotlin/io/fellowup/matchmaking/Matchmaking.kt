package io.fellowup.matchmaking

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

class MatchmakingEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MatchmakingEntity>(Matchmakings)

    var category: String by Matchmakings.category
    var userId: UUID by Matchmakings.user_id
    var at: Instant by Matchmakings.at
}

object Matchmakings : UUIDTable("matchmaking") {
    val category = text("category")
    val user_id = uuid("user_id")
    val at = timestamp("at")
}
