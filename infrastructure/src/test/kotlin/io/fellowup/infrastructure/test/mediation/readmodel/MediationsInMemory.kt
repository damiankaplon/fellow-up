package io.fellowup.infrastructure.test.mediation.readmodel

import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.Mediation
import io.fellowup.domain.mediation.readmodel.Mediations

internal class MediationsInMemory : Mediations {

    private var mediations: Set<Mediation> = emptySet()

    override suspend fun findNotFinishedByParticipant(participantId: ParticipantId): Set<Mediation> {
        return mediations.filter { it.participantIds.contains(participantId) }.toSet()
    }

    fun add(mediation: Mediation) {
        mediations = mediations + mediation
    }
}
