package io.fellowup.matchmaking.infra

import io.fellowup.db.TransactionalRunner
import io.fellowup.kotlinx.serialization.Uuid
import io.fellowup.kotlinx.serialization.toKotlinx
import io.fellowup.matchmaking.Location
import io.fellowup.matchmaking.Matchmaking
import io.fellowup.matchmaking.MatchmakingRepository
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
        val at: Instant,
        val location: LocationDto
    )

    @Serializable
    data class LocationDto(
        val lat: Double,
        val lng: Double
    )

    @Serializable
    data class MatchmakingDto(
        val id: Uuid,
        val category: String,
        val at: Instant,
        val location: LocationDto
    )

    private fun CreateMatchmakingBody.toDomain(principal: Principal): Matchmaking {
        return Matchmaking(
            category = this.category,
            at = this.at.toJavaInstant(),
            userId = principal.userId,
            location = Location(this.location.lat, this.location.lng)
        )
    }

    private fun Matchmaking.toDto(): MatchmakingDto = MatchmakingDto(
        id = id.value.toKotlinx(),
        category = category,
        at = at.toKotlinInstant(),
        location = LocationDto(location.latitude, location.longitude)
    )
}