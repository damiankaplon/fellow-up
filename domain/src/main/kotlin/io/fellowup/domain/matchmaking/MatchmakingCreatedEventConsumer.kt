package io.fellowup.domain.matchmaking

import io.fellowup.domain.events.EventConsumer

class MatchmakingCreatedEventConsumer(
    private val matchmakingService: MatchmakingService
) : EventConsumer<MatchmakingEvent.MatchmakingCreated> {
    override suspend fun handle(event: MatchmakingEvent.MatchmakingCreated) {
        matchmakingService.match(event.matchmakingId)
    }
}