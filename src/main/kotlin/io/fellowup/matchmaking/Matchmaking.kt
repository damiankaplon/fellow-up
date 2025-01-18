package io.fellowup.matchmaking

import java.time.Instant
import java.util.UUID

class Matchmaking(
    val id: Id = Id(),
    val category: String,
    val userId: UUID,
    val at: Instant
) {

    @JvmInline
    value class Id(val value: UUID = UUID.randomUUID())
}
