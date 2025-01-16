package io.fellowup.matchmaking

import io.fellowup.db.TransactionalRunner
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.installMatchmakingModule(transactionalRunner: TransactionalRunner) {
    routing { registerMatchmakingEndpoints(transactionalRunner) }
}