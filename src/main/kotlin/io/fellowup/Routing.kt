package io.fellowup

import io.fellowup.matchmaking.MatchmakingsController
import io.fellowup.matchmaking.MatchmakingsController.CreateMatchmakingBody
import io.fellowup.matchmaking.MatchmakingsController.MatchmakingDto
import io.fellowup.security.SecuredRouting
import io.fellowup.security.jwtPrincipalOrThrow
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val API_PREFIX = "api"

fun Routing.installAppRouting(
    securedRouting: SecuredRouting,
    matchmakingsController: MatchmakingsController
) {
    securedRouting(this) {
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