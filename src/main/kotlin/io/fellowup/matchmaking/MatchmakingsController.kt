package io.fellowup.matchmaking

import io.fellowup.db.TransactionalRunner
import io.fellowup.kotlinx.serialization.Uuid
import io.fellowup.kotlinx.serialization.toKotlinx
import io.fellowup.security.Principal
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable

class MatchmakingsController(
    private val transactionalRunner: TransactionalRunner,
) {

    fun createMatchmaking(body: CreateMatchmakingBody, principal: Principal): MatchmakingDto =
        transactionalRunner.transaction { MatchmakingEntity.new { from(body, principal) } }.toDto()


    fun getMatchmakings(principal: Principal): Collection<MatchmakingDto> =
        transactionalRunner.transaction(readOnly = true) {
            return@transaction MatchmakingEntity.find { Matchmakings.user_id eq principal.userId }
                .map { it.toDto() }.toSet()
        }

    @Serializable
    data class CreateMatchmakingBody(
        val category: String,
        val at: Instant
    )

    @Serializable
    data class MatchmakingDto(
        val id: Uuid?,
        val userId: Uuid?,
        val category: String,
        val at: Instant
    )

    fun MatchmakingEntity.from(body: CreateMatchmakingBody, principal: Principal) {
        this.category = body.category
        this.at = body.at.toJavaInstant()
        this.userId = principal.userId
    }


    fun MatchmakingEntity.toDto() = MatchmakingDto(
        id = id.value.toKotlinx(),
        category = category,
        userId = null,
        at = at.toKotlinInstant()
    )
}