package io.fellowup.domain.mediation.readmodel

import io.fellowup.domain.mediation.ParticipantId
import java.util.*

interface Mediation {
    val id: UUID
    val category: String
    val participantIds: Set<ParticipantId>
    val proposals: Set<Proposal>
}

