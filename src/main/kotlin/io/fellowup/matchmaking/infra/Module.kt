package io.fellowup.matchmaking.infra

import io.fellowup.db.TransactionalRunner
import io.fellowup.matchmaking.MatchmakingRepository

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
