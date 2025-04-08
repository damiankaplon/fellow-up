package io.fellowup.infrastructure.events.outbox.infra

import io.fellowup.infrastructure.databind.Deserializer
import io.fellowup.infrastructure.databind.Serializer
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.jsonb

private val JSON_NODE_SERIALIZER = Serializer<Any>()
private val JSON_NODE_DESERIALIZER = Deserializer<Any>()

class OutboxPublishedDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OutboxPublishedDao>(OutboxPublishedTable)

    var sentTo by OutboxPublishedTable.sentTo
    var message by OutboxPublishedTable.message

    object OutboxPublishedTable : LongIdTable("outbox_published") {
        val sentTo = text("sent_to")
        val message = jsonb("message", JSON_NODE_SERIALIZER::invoke, JSON_NODE_DESERIALIZER::invoke)
    }
}
