package io.fellowup.infrastructure.mediation.readmodel

import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.*
import io.fellowup.infrastructure.security.Principal
import io.ktor.util.logging.*
import kotlinx.serialization.Serializable
import java.util.*
import io.fellowup.domain.mediation.Mediation as MediationDomain

private val LOGGER = KtorSimpleLogger(MediationsController::class.java.name)

class MediationsController(
    private val mediations: Mediations,
    private val fellows: Fellows,
) {

    suspend fun findNotFinishedByFellowId(principal: Principal): Set<MediationDto> {
        val mediations: Set<Mediation> = mediations.findNotFinishedByParticipant(ParticipantId(principal.userId))
        val participants: Set<ParticipantId> = mediations.flatMap(Mediation::participantIds).toSet()
        val fellows: Set<Fellow> = fellows.findByParticipantIds(participants)
        return mediations.map { toDto(it, fellows) }.toSet()
    }

    suspend fun findById(id: MediationDomain.Id, principal: Principal): MediationDto? {
        val mediation: Mediation? = mediations.findById(id)
        return if (mediation?.participantIds?.contains(ParticipantId(principal.userId)) == true) {
            toDto(mediation, fellows.findByParticipantIds(mediation.participantIds))
        } else {
            LOGGER.error("Mediation $id not found or not owned by $principal. ${principal.userId} tried to access mediation with id: $id")
            null
        }
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
            location = LocationDto(
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
        val location: LocationDto
    )

    @Serializable
    data class LocationDto(
        val lat: Double,
        val lng: Double
    )
}
