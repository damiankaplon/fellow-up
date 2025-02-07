package io.fellowup.matchmaking

import io.fellowup.db.TransactionalRunner
import io.ktor.server.application.*

fun installMatchmakingModule(
    transactionalRunner: TransactionalRunner,
    matchmakingRepository: MatchmakingRepository = MatchmakingDaoRepository()
): MatchmakingsModule {
    return MatchmakingsModule(
        matchmakingsController = MatchmakingsController(transactionalRunner, matchmakingRepository)
    )
}

class MatchmakingsModule(
    val matchmakingsController: MatchmakingsController
)