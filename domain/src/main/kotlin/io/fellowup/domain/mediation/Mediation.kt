package io.fellowup.domain.mediation

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.matchmaking.Matchmaking
import java.time.Instant
import java.util.*

class Mediation(
    val id: Id = Id(),
    val category: String,
    private val participants: Set<ParticipantId> = emptySet()
) {

    var proposals: Set<ActivityProposal> = emptySet(); private set
    var isFinished: Boolean = false; private set

    fun propose(location: Location, time: Instant) {
        check(isFinished.not()) { "Mediation is finished" }

        val lastProposalId = proposals.maxOfOrNull { it.order.value } ?: 0
        val order = ActivityProposal.Order(lastProposalId + 1)
        proposals += ActivityProposal(order, location, time)
    }

    fun accept(participantId: ParticipantId, proposalOrder: ActivityProposal.Order) {
        check(isFinished.not()) { "Mediation is finished" }

        proposals.filter { it.acceptedByParticipantIds.contains(participantId) }
            .forEach { it.unAccept(participantId) }

        val proposal = proposals.find { it.order == proposalOrder }
            ?: error("No proposal found for id $proposalOrder")

        proposal.accept(participantId)
        if (proposal.acceptedByParticipantIds.containsAll(participants)) {
            isFinished = true
        }
    }


    @JvmInline
    value class Id(val value: UUID = UUID.randomUUID())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mediation

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

fun Mediation(matchmakings: Set<Matchmaking>): Mediation {
    require(matchmakings.map { it.userId }.toSet().size == matchmakings.size)
    { "All matchmakings must belong to different users" }
    require(matchmakings.map { it.category }.toSet().size == 1)
    { "All matchmakings must have the same category" }
    val mediation = Mediation(
        category = matchmakings.first().category,
        participants = matchmakings.map { ParticipantId(it.userId) }.toSet()
    )
    matchmakings.forEach { mediation.propose(it.location, it.at) }
    return mediation
}
