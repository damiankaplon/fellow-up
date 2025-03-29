package io.fellowup.matchmaking

import io.fellowup.events.EventConsumer

class MatchmakingCreatedEventConsumer(
    private val matchmakingService: MatchmakingService
) : EventConsumer<MatchmakingEvent.MatchmakingCreated> {
    override suspend fun handle(event: MatchmakingEvent.MatchmakingCreated) {
        matchmakingService.match(event.matchmakingId)
    }
}