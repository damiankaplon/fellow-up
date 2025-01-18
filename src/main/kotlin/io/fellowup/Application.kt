package io.fellowup

import io.fellowup.db.installDatabaseModule
import io.fellowup.matchmaking.MatchmakingsController.CreateMatchmakingBody
import io.fellowup.matchmaking.MatchmakingsController.MatchmakingDto
import io.fellowup.matchmaking.installMatchmakingModule
import io.fellowup.security.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val API_PREFIX = "api"

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    installSerialization()
    install(StatusPages) {
        exception(NoJwtExceptionHandler)
        exception(NoAuthenticatedSubjectExceptionHandler)
    }
    val dbModule = installDatabaseModule()
    val oAuthModule = installOAuthAuth()
    val matchmakingModule = installMatchmakingModule(dbModule.transactionalRunner)
    val matchmakingsController = matchmakingModule.matchmakingsController
    routing {
        registerOAuthCodeFlowEndpoints(oauthLogoutUrl = oAuthModule.oauthConfigProvider.logoutUrl)
        secure {
            post("$API_PREFIX/matchmakings") {
                val body = call.receive<CreateMatchmakingBody>()
                call.respond<MatchmakingDto>(
                    matchmakingsController.createMatchmaking(body, call.jwtPrincipalOrThrow())
                )
            }
            get("$API_PREFIX/matchmakings") {
                call.respond<Collection<MatchmakingDto>>(
                    matchmakingsController.getMatchmakings(call.jwtPrincipalOrThrow())
                )
            }
        }
    }
}
