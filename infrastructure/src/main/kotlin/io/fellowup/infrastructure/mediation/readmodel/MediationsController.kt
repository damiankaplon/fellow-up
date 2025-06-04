package io.fellowup.infrastructure.mediation.readmodel

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.Fellow
import io.fellowup.domain.mediation.readmodel.Fellows
import io.fellowup.domain.mediation.readmodel.MediationMatchmakings
import io.fellowup.domain.mediation.readmodel.Mediations
import io.fellowup.infrastructure.security.Principal
import io.ktor.util.logging.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import io.fellowup.domain.mediation.readmodel.Mediation as MediationReadModel

private val LOGGER = KtorSimpleLogger(MediationsController::class.java.name)

class MediationsController(
    private val mediations: Mediations,
    private val mediationMatchmakings: MediationMatchmakings,
    private val fellows: Fellows,
) {

    suspend fun findNotFinishedByFellowId(principal: Principal): Set<MediationDto> {
        val mediations: Set<MediationReadModel> =
            mediations.findNotFinishedByParticipant(ParticipantId(principal.userId))
        val participants: Set<ParticipantId> = mediations.flatMap(MediationReadModel::participantIds).toSet()
        val participatingFellows: Set<Fellow> = fellows.findByParticipantIds(participants)
        return mediations.map { mediationReadModel: MediationReadModel ->
            MediationDto.Builder(
                mediation = mediationReadModel,
                fellows = participatingFellows
            ).build()
        }.toSet()
    }

    suspend fun findByMatchmakingId(id: Matchmaking.Id, principal: Principal): MediationDto? {
        val mediationId: Mediation.Id = mediationMatchmakings.findMediationOrThrow(id)
        val mediation: MediationReadModel? = mediations.findByIdOrThrow(mediationId)
        return if (mediation?.participantIds?.contains(ParticipantId(principal.userId)) == true) {
            MediationDto.Builder(
                mediation = mediation,
                fellows = fellows.findByParticipantIds(mediation.participantIds)
            ).build()
        } else {
            LOGGER.error("Mediation $id not found or not owned by $principal. ${principal.userId} tried to access mediation with id: $id")
            null
        }
    }

    @Serializable
    data class MediationDto(
        val category: String,
        val fellows: Set<FellowDto>,
        val proposals: Set<ProposalDto>
    ) {

        class Builder(val mediation: MediationReadModel, val fellows: Set<Fellow>) {

            fun build(): MediationDto {
                return MediationDto(
                    category = mediation.category,
                    fellows = fellows.mapTo(linkedSetOf(), ::FellowDto),
                    proposals = mediation.proposals.mapTo(linkedSetOf()) {
                        ProposalDto.Builder(
                            fellows,
                            it.acceptedBy,
                            it.location.let(::LocationDto),
                            it.time.toKotlinInstant()
                        ).invoke()
                    }
                )
            }
        }
    }

    @Serializable
    data class FellowDto(
        val id: String,
        val name: String,
    ) {
        constructor(fellow: Fellow) : this(fellow.participantId, fellow.name)
    }

    @Serializable
    data class ProposalDto(
        val acceptedBy: Set<FellowDto>,
        val location: LocationDto,
        val time: Instant
    ) {

        class Builder(
            var fellows: Set<Fellow>,
            var acceptedBy: Set<ParticipantId>,
            var location: LocationDto,
            var time: Instant
        ) {
            operator fun invoke(): ProposalDto {
                val acceptedBy: Set<FellowDto> = acceptedBy.associateWith { id: ParticipantId ->
                    fellows.find { fellow: Fellow -> fellow.participantId == id.id.toString() }
                }.mapNotNull { it.value }.map(::FellowDto).toSet()
                return ProposalDto(acceptedBy, location, time)
            }
        }
    }

    @Serializable
    data class LocationDto(
        val lat: Double,
        val lng: Double
    ) {
        constructor(location: Location) : this(location.latitude, location.longitude)
    }
}
