package io.fellowup.domain.mediation.readmodel

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.mediation.ParticipantId
import java.time.Instant

interface Proposal {
    val acceptedBy: Set<ParticipantId>
    val location: Location
    val time: Instant
}
