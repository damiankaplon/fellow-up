package io.fellowup.infrastructure.mediation.readmodel

import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.*
import kotlinx.serialization.Serializable
import java.util.*

class MediationsController(
    private val mediations: Mediations,
    private val fellows: Fellows,
) {

    suspend fun findNotFinishedByFellowId(fellowId: UUID): Set<MediationDto> {
        val mediations: Set<Mediation> = mediations.findNotFinishedByParticipant(ParticipantId(fellowId))
        val participants: Set<ParticipantId> = mediations.flatMap(Mediation::participantIds).toSet()
        val fellows: Set<Fellow> = fellows.findByParticipantIds(participants)
        return mediations.map { toDto(it, fellows) }.toSet()
    }

    private fun toDto(mediation: Mediation, fellows: Set<Fellow>): MediationDto {
        val mediationFellows: Set<Fellow> = fellows.filter { fellow: Fellow ->
            mediation.participantIds.map(ParticipantId::id).map(UUID::toString).contains(fellow.participantId)
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
            acceptedBy = proposal.acceptedBy,
            locationDto = LocationDto(
                lat = proposal.location.latitude,
                lng = proposal.location.longitude
            )
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
        val acceptedBy: Int,
        val locationDto: LocationDto
    )

    @Serializable
    data class LocationDto(
        val lat: Double,
        val lng: Double
    )
}
