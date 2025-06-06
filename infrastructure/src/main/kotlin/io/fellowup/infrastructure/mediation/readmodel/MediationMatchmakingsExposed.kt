package io.fellowup.infrastructure.mediation.readmodel

import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.readmodel.MediationMatchmakings
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert

class MediationMatchmakingsExposed(
    private val transactionalRunner: TransactionalRunner
) : MediationMatchmakings {

    override fun save(
        mediation: Mediation.Id,
        matchmakings: Set<Matchmaking.Id>
    ) {
        MediationMatchmakingsTable.batchInsert(matchmakings) { matchmaking: Matchmaking.Id ->
            this[MediationMatchmakingsTable.mediationId] = mediation.value
            this[MediationMatchmakingsTable.matchmakingId] = matchmaking.value
        }
    }

    override suspend fun findMediation(matchmaking: Matchmaking.Id): Mediation.Id? =
        transactionalRunner.transaction(readOnly = true) {
            return@transaction MediationMatchmakingsTable.select(
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
