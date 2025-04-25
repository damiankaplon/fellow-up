package io.fellowup.domain.mediation

import io.fellowup.domain.matchmaking.Location
import java.time.Instant

class ActivityProposal(
    val order: Order,
    val location: Location,
    val time: Instant
) {

    var acceptedByParticipantIds: Set<ParticipantId> = emptySet(); private set

    internal fun accept(participantId: ParticipantId) {
        acceptedByParticipantIds = acceptedByParticipantIds + participantId
    }

    internal fun unAccept(participantId: ParticipantId) {
        acceptedByParticipantIds = acceptedByParticipantIds - participantId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ActivityProposal

        return order == other.order
    }

    override fun hashCode(): Int {
        return order.hashCode()
    }

    @JvmInline
    value class Order(val value: Int)
}
