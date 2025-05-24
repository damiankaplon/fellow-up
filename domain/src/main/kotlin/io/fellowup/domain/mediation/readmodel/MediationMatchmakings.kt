package io.fellowup.domain.mediation.readmodel

import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.mediation.Mediation

interface MediationMatchmakings {

    fun save(mediation: Mediation.Id, matchmakings: Set<Matchmaking.Id>)

    fun findMatchmakings(mediation: Mediation.Id): Set<Matchmaking.Id>

    fun findMediation(matchmaking: Matchmaking.Id): Mediation.Id?

    fun findMediationOrThrow(matchmaking: Matchmaking.Id): Mediation.Id =
        findMediation(matchmaking) ?: error("Did not find mediation of matchmaking with id: $matchmaking")
}
