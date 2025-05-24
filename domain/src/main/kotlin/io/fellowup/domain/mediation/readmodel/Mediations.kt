package io.fellowup.domain.mediation.readmodel

import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.Mediation as MediationDomain

interface Mediations {

    suspend fun findNotFinishedByParticipant(participantId: ParticipantId): Set<Mediation>

    suspend fun findById(id: MediationDomain.Id): Mediation?

    suspend fun findByIdOrThrow(id: MediationDomain.Id): Mediation =
        findById(id) ?: error("Mediation id: $id not found")
}
