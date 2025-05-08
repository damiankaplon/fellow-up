package io.fellowup.domain.mediation.readmodel

import io.fellowup.domain.mediation.ParticipantId

interface Fellows {

    suspend fun findByParticipantId(participantId: ParticipantId): Fellow
    suspend fun findByParticipantIds(participantIds: Set<ParticipantId>): Set<Fellow>
}
