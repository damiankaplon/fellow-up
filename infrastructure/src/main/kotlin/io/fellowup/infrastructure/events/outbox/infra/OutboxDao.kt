package io.fellowup.infrastructure.events.outbox.infra

import io.fellowup.infrastructure.databind.Deserializer
import io.fellowup.infrastructure.databind.Serializer
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.jsonb


private val SERIALIZER = Serializer<Any>()
private val DESERIALIZER = Deserializer<Any>()

class OutboxDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OutboxDao>(OutboxTable)

    var destination: String by OutboxTable.destination
    var message: Any by OutboxTable.message

    object OutboxTable : LongIdTable("outbox") {
        val destination = text("destination")
        val message = jsonb("message", SERIALIZER::invoke, DESERIALIZER::invoke)
    }
}
