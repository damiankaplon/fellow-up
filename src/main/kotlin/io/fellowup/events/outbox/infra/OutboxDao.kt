package io.fellowup.events.outbox.infra

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.jsonb


private val SERIALIZER = io.fellowup.databind.infra.Serializer<Any>()
private val DESERIALIZER = io.fellowup.databind.infra.Deserializer<Any>()

class OutboxDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OutboxDao>(OutboxTable)

    var destination: String by OutboxTable.destination
    var message: Any by OutboxTable.message

    object OutboxTable : LongIdTable("outbox") {
        val destination = text("destination")
        val message = jsonb("message", SERIALIZER::invoke, DESERIALIZER::invoke)
    }
}