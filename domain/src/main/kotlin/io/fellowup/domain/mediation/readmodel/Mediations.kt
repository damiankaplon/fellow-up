package io.fellowup.domain.mediation.readmodel

import io.fellowup.domain.mediation.ParticipantId

interface Mediations {

    suspend fun findNotFinishedByParticipant(participantId: ParticipantId): Set<Mediation>
}
