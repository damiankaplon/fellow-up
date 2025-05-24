package io.fellowup.infrastructure.test.mediation.readmodel

import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.Mediation
import io.fellowup.domain.mediation.readmodel.Mediations

internal class MediationsInMemory : Mediations {

    private var mediations: Set<Mediation> = emptySet()

    override suspend fun findNotFinishedByParticipant(participantId: ParticipantId): Set<Mediation> {
        return mediations.filter { it.participantIds.contains(participantId) }.toSet()
    }

    override suspend fun findById(id: io.fellowup.domain.mediation.Mediation.Id): Mediation? {
        return mediations.singleOrNull { it.id == id.value }
    }

    fun add(mediation: Mediation) {
        mediations = mediations + mediation
    }
}
