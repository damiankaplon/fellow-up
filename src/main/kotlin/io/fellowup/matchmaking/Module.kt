package io.fellowup.matchmaking

import io.fellowup.db.TransactionalRunner
import io.ktor.server.application.*

fun Application.installMatchmakingModule(
    transactionalRunner: TransactionalRunner,
): MatchmakingsModule {
    return MatchmakingsModule(
        matchmakingsController = MatchmakingsController(transactionalRunner)
    )
}

class MatchmakingsModule(
    val matchmakingsController: MatchmakingsController
)