package io.fellowup.matchmaking

import io.fellowup.matchmaking.Matchmaking.Id
import java.time.Instant
import java.util.UUID

class Activity(
    val id: Id = Id(),
    val category: String,
    val at: Instant,
    val location: Location
) {

    @JvmInline
    value class Id(val value: UUID = UUID.randomUUID())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Activity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}