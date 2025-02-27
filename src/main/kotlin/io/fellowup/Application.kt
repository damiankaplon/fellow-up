package io.fellowup

import io.fellowup.db.installDatabase
import io.fellowup.db.createTransactionalRunner
import io.fellowup.matchmaking.infra.createMatchmakingModule
import io.fellowup.security.NoAuthenticatedSubjectExceptionHandler
import io.fellowup.security.NoJwtExceptionHandler
import io.fellowup.security.installOAuthAuth
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    installSerialization()
    install(StatusPages) {
        exception(NoJwtExceptionHandler)
        exception(NoAuthenticatedSubjectExceptionHandler)
    }
    val db = installDatabase()
    val transactionalRunner = createTransactionalRunner(db)
    val oAuthModule = installOAuthAuth()
    val matchmakingModule = createMatchmakingModule(transactionalRunner)
    routing {
        installAppRouting(oAuthModule.securedRouting, matchmakingModule.matchmakingsController)
    }
}
