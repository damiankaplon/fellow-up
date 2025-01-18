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
    private val matchmakingRepository: MatchmakingRepository
) {

    fun createMatchmaking(body: CreateMatchmakingBody, principal: Principal): MatchmakingDto =
        transactionalRunner.transaction { matchmakingRepository.save(body.toDomain(principal)) }.toDto()


    fun getMatchmakings(principal: Principal): Collection<MatchmakingDto> =
        transactionalRunner.transaction(readOnly = true) {
            return@transaction matchmakingRepository.findAllByUserId(principal.userId)
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

    private fun CreateMatchmakingBody.toDomain(principal: Principal): Matchmaking {
        return Matchmaking(
            category = this.category,
            at = this.at.toJavaInstant(),
            userId = principal.userId
        )
    }


    private fun Matchmaking.toDto(): MatchmakingDto = MatchmakingDto(
        id = id.value.toKotlinx(),
        category = category,
        userId = null,
        at = at.toKotlinInstant()
    )
}
