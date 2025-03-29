package io.fellowup.matchmaking.infra

import io.fellowup.db.TransactionalRunner
import io.fellowup.events.EventPublisher
import io.fellowup.matchmaking.*

fun createMatchmakingModule(
    transactionalRunner: TransactionalRunner,
    matchmakingEventsPublisher: EventPublisher<MatchmakingEvent>,
    matchmakingRepository: MatchmakingRepository = MatchmakingDaoRepository(),
    activityRepository: ActivityRepository = ActivityDaoRepository(),
): MatchmakingsModule {
    val matchmakingService = MatchmakingService(
        matchmakingRepository,
        activityRepository,
        PlainKotlinDistanceCalculator(),
        matchmakingEventsPublisher
    )
    return MatchmakingsModule(
        matchmakingsController = MatchmakingsController(
            transactionalRunner,
            matchmakingRepository,
            matchmakingEventsPublisher
        ),
        matchmakingCreatedEventConsumer = MatchmakingCreatedEventConsumer(matchmakingService)

    )
}

class MatchmakingsModule(
    val matchmakingsController: MatchmakingsController,
    val matchmakingCreatedEventConsumer: MatchmakingCreatedEventConsumer
)
