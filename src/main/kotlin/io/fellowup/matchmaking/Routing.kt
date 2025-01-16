package io.fellowup.matchmaking

import io.fellowup.db.TransactionalRunner
import io.fellowup.kotlinx.serialization.Uuid
import io.fellowup.kotlinx.serialization.toKotlinx
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import java.util.*

fun Routing.registerMatchmakingEndpoints(transactionalRunner: TransactionalRunner) {
    post("/matchmaking") {
        val dto = this.call.receive(MatchmakingDto::class)
        val matchmaking = transactionalRunner.transaction {
            MatchmakingEntity.new { from(dto) }
        }
        call.respond(matchmaking.toDto())
    }
}

@Serializable
data class MatchmakingDto(
    val id: Uuid?,
    // This should be a userId of currently authenticated user
    val userId: Uuid?,
    val category: String,
    val at: Instant
)

fun MatchmakingEntity.from(dto: MatchmakingDto) {
    this.category = dto.category
    this.at = dto.at.toJavaInstant()
    this.userId = UUID.randomUUID()
}


fun MatchmakingEntity.toDto() = MatchmakingDto(
    id = id.value.toKotlinx(),
    category = category,
    userId = null,
    at = at.toKotlinInstant()
)