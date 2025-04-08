package io.fellowup.infrastructure.matchmaking.infra

import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.matchmaking.*

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
