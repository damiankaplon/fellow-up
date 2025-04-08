package io.fellowup.domain.matchmaking

sealed interface MatchmakingEvent {
    val matchmakingId: Matchmaking.Id

    data class MatchmakingCreated(
        override val matchmakingId: Matchmaking.Id,
    ) : MatchmakingEvent

    data class ActivityMatched(
        override val matchmakingId: Matchmaking.Id,
        val activityId: Activity.Id,
    ) : MatchmakingEvent
}
