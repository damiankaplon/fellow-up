package io.fellowup.infrastructure.mediation.readmodel

import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.*
import io.fellowup.infrastructure.kotlinx.serialization.Uuid
import io.fellowup.infrastructure.security.Principal
import io.ktor.util.logging.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import java.util.*
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
        val fellows: Set<Fellow> = fellows.findByParticipantIds(participants)
        return mediations.map { toDto(it, fellows) }.toSet()
    }

    suspend fun findByMatchmakingId(id: Matchmaking.Id, principal: Principal): MediationDto? {
        val mediationId: Mediation.Id = mediationMatchmakings.findMediationOrThrow(id)
        val mediation: MediationReadModel? = mediations.findByIdOrThrow(mediationId)
        return if (mediation?.participantIds?.contains(ParticipantId(principal.userId)) == true) {
            toDto(mediation, fellows.findByParticipantIds(mediation.participantIds))
        } else {
            LOGGER.error("Mediation $id not found or not owned by $principal. ${principal.userId} tried to access mediation with id: $id")
            null
        }
    }

    private fun toDto(mediation: MediationReadModel, fellows: Set<Fellow>): MediationDto {
        val mediationFellows: Set<Fellow> = fellows.filter { fellow: Fellow ->
            mediation.participantIds.map(ParticipantId::id)
                .map(UUID::toString)
                .contains(fellow.participantId)
        }.toSet()
        return MediationDto(
            category = mediation.category,
            fellows = mediationFellows.map(::toDto).toSet(),
            proposals = mediation.proposals.map(::toDto).toSet()
        )
    }

    private fun toDto(fellow: Fellow): FellowDto {
        return FellowDto(
            id = fellow.participantId,
            name = fellow.name
        )
    }

    private fun toDto(proposal: Proposal): ProposalDto {
        return ProposalDto(
            acceptedBy = proposal.acceptedBy.map(ParticipantId::id).map(::Uuid).toSet(),
            location = LocationDto(
                lat = proposal.location.latitude,
                lng = proposal.location.longitude
            ),
            time = proposal.time.toKotlinInstant()
        )
    }

    @Serializable
    data class MediationDto(
        val category: String,
        val fellows: Set<FellowDto>,
        val proposals: Set<ProposalDto>
    )

    @Serializable
    data class FellowDto(
        val id: String,
        val name: String,
    )

    @Serializable
    data class ProposalDto(
        val acceptedBy: Set<Uuid>,
        val location: LocationDto,
        val time: Instant
    )

    @Serializable
    data class LocationDto(
        val lat: Double,
        val lng: Double
    )
}
