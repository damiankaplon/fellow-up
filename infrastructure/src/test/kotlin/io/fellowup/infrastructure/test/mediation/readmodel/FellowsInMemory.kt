package io.fellowup.infrastructure.test.mediation.readmodel

import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.Fellow
import io.fellowup.domain.mediation.readmodel.Fellows

internal class FellowsInMemory : Fellows {

    private var fellows: Set<Fellow> = emptySet()

    override suspend fun findByParticipantId(participantId: ParticipantId): Fellow {
        return fellows.single { it.participantId == participantId.toString() }
    }

    override suspend fun findByParticipantIds(participantIds: Set<ParticipantId>): Set<Fellow> {
        return fellows.filter { participantIds.map { it.id.toString() }.contains(it.participantId) }.toSet()
    }

    fun add(fellow: Fellow) {
        fellows = fellows + fellow
    }
}
