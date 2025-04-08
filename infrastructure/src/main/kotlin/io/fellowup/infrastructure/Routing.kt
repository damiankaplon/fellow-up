package io.fellowup.infrastructure

import io.fellowup.infrastructure.matchmaking.infra.MatchmakingsController
import io.fellowup.infrastructure.matchmaking.infra.MatchmakingsController.CreateMatchmakingBody
import io.fellowup.infrastructure.matchmaking.infra.MatchmakingsController.MatchmakingDto
import io.fellowup.infrastructure.security.SecuredRouting
import io.fellowup.infrastructure.security.jwtPrincipalOrThrow
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val API_PREFIX = "api"

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