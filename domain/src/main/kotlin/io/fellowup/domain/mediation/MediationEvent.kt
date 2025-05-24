package io.fellowup.domain.mediation

import io.fellowup.domain.matchmaking.Matchmaking

sealed interface MediationEvent {
    val mediationId: Mediation.Id

    data class MediationStarted(
        override val mediationId: Mediation.Id,
        val includedMatchmakings: Set<Matchmaking.Id>
    ) : MediationEvent
}
