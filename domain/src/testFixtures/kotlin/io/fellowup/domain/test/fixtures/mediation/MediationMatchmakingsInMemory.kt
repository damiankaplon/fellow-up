package io.fellowup.domain.test.fixtures.mediation

import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.readmodel.MediationMatchmakings

class MediationMatchmakingsInMemory : MediationMatchmakings {

    val mediationMatchmakings: MutableMap<Mediation.Id, Set<Matchmaking.Id>> = mutableMapOf()

    override fun save(
        mediation: Mediation.Id,
        matchmakings: Set<Matchmaking.Id>
    ) {
        mediationMatchmakings.put(mediation, matchmakings)
    }

    override suspend fun findMediation(matchmaking: Matchmaking.Id): Mediation.Id? {
        return mediationMatchmakings.entries.find { it.value.contains(matchmaking) }?.key
    }
}
