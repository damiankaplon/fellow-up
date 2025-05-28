package io.fellowup.infrastructure.matchmaking

import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.matchmaking.MatchmakingEvent
import io.fellowup.domain.matchmaking.MatchmakingRepository
import io.fellowup.domain.mediation.readmodel.MediationMatchmakings
import io.fellowup.infrastructure.kotlinx.serialization.Uuid
import io.fellowup.infrastructure.kotlinx.serialization.toKotlinx
import io.fellowup.infrastructure.security.Principal
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable

class MatchmakingController(
    private val transactionalRunner: TransactionalRunner,
    private val matchmakingRepository: MatchmakingRepository,
    private val mediationMatchmakings: MediationMatchmakings,
    private val matchmakingEventsPublisher: EventPublisher<MatchmakingEvent>
) {

    suspend fun createMatchmaking(body: CreateMatchmakingBody, principal: Principal): MatchmakingDto =
        transactionalRunner.transaction {
            val matchmaking = matchmakingRepository.save(body.toDomain(principal))
            matchmakingEventsPublisher.publish(MatchmakingEvent.MatchmakingCreated(matchmaking.id))
            return@transaction matchmaking.toDto(status = MatchmakingDto.Status.STILL_LOOKING)
        }


    suspend fun getMatchmakings(principal: Principal): Collection<MatchmakingDto> =
        transactionalRunner.transaction(readOnly = true) {
            return@transaction matchmakingRepository.findAllByUserId(principal.userId)
                .map {
                    val mediation = mediationMatchmakings.findMediation(it.id)
                    val status =
                        if (mediation != null) MatchmakingDto.Status.MEDIATING else MatchmakingDto.Status.STILL_LOOKING
                    it.toDto(status)
                }.toSet()
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
        val status: Status,
        val category: String,
        val at: Instant,
        val location: LocationDto
    ) {
        enum class Status {
            STILL_LOOKING,
            MEDIATING
        }
    }

    private fun CreateMatchmakingBody.toDomain(principal: Principal): Matchmaking {
        return Matchmaking(
            category = this.category,
            at = this.at.toJavaInstant(),
            userId = principal.userId,
            location = Location(this.location.lng, this.location.lat)
        )
    }

    private fun Matchmaking.toDto(status: MatchmakingDto.Status): MatchmakingDto = MatchmakingDto(
        id = id.value.toKotlinx(),
        category = category,
        status = status,
        at = at.toKotlinInstant(),
        location = LocationDto(location.latitude, location.longitude)
    )
}