package io.fellowup.infrastructure.mediation.readmodel

import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.readmodel.MediationMatchmakings
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert

class MediationMatchmakingsExposed : MediationMatchmakings {

    override fun save(
        mediation: Mediation.Id,
        matchmakings: Set<Matchmaking.Id>
    ) {
        MediationMatchmakingsTable.batchInsert(matchmakings) { matchmaking: Matchmaking.Id ->
            this[MediationMatchmakingsTable.mediationId] = mediation.value
            this[MediationMatchmakingsTable.matchmakingId] = matchmaking.value
        }
    }

    override fun findMatchmakings(mediation: Mediation.Id): Set<Matchmaking.Id> {
        return MediationMatchmakingsTable.select(
            MediationMatchmakingsTable.mediationId,
            MediationMatchmakingsTable.matchmakingId
        ).where { MediationMatchmakingsTable.mediationId eq mediation.value }
            .groupBy { resultRow: ResultRow -> resultRow[MediationMatchmakingsTable.mediationId] }
            .entries.singleOrNull()?.value?.map { resultRow: ResultRow ->
                Matchmaking.Id(resultRow[MediationMatchmakingsTable.matchmakingId])
            }?.toSet() ?: emptySet()
    }

    override fun findMediation(matchmaking: Matchmaking.Id): Mediation.Id? {
        return MediationMatchmakingsTable.select(
            MediationMatchmakingsTable.mediationId,
            MediationMatchmakingsTable.matchmakingId
        ).where { MediationMatchmakingsTable.matchmakingId eq matchmaking.value }
            .groupBy { resultRow: ResultRow -> resultRow[MediationMatchmakingsTable.mediationId] }
            .keys.singleOrNull()?.let { Mediation.Id(it) }
    }
}

private object MediationMatchmakingsTable : CompositeIdTable("mediation_matchmakings") {
    val mediationId = uuid("mediation_id")
    val matchmakingId = uuid("matchmaking_id")
    override val primaryKey = PrimaryKey(mediationId, matchmakingId)
}
