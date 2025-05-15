package io.fellowup.infrastructure

import io.fellowup.infrastructure.matchmaking.MatchmakingController
import io.fellowup.infrastructure.matchmaking.MatchmakingController.CreateMatchmakingBody
import io.fellowup.infrastructure.matchmaking.MatchmakingController.MatchmakingDto
import io.fellowup.infrastructure.mediation.readmodel.MediationsController
import io.fellowup.infrastructure.security.SecuredRouting
import io.fellowup.infrastructure.security.jwtPrincipalOrThrow
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val API_PREFIX = "api"

fun Routing.installAppRouting(
    securedRouting: SecuredRouting,
    matchmakingController: MatchmakingController,
    mediationsController: MediationsController
) {
    securedRouting(this) {
        post("$API_PREFIX/matchmakings") {
            val body = call.receive<CreateMatchmakingBody>()
            call.respond<MatchmakingDto>(
                matchmakingController.createMatchmaking(body, call.jwtPrincipalOrThrow())
            )
        }
        get("$API_PREFIX/matchmakings") {
            call.respond<Collection<MatchmakingDto>>(
                matchmakingController.getMatchmakings(call.jwtPrincipalOrThrow())
            )
        }
        get("$API_PREFIX/mediations") {
            call.respond<Set<MediationsController.MediationDto>>(
                mediationsController.findNotFinishedByFellowId(call.jwtPrincipalOrThrow())
            )
        }
    }
}
