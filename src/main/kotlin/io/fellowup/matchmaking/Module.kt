package io.fellowup.matchmaking

import io.fellowup.db.TransactionalRunner

fun createMatchmakingModule(
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