package io.fellowup.infrastructure.matchmaking.infra

import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.matchmaking.*
import io.fellowup.domain.mediation.MediationRepository
import io.fellowup.domain.mediation.readmodel.Fellows
import io.fellowup.domain.mediation.readmodel.Mediations
import io.fellowup.infrastructure.mediation.MediationDaoRepository
import io.fellowup.infrastructure.mediation.readmodel.MediationsController
import io.fellowup.infrastructure.mediation.readmodel.MediationsExposed

fun createMatchmakingModule(
    transactionalRunner: TransactionalRunner,
    matchmakingEventsPublisher: EventPublisher<MatchmakingEvent>,
    fellows: Fellows,
    matchmakingRepository: MatchmakingRepository = MatchmakingDaoRepository(),
    activityRepository: ActivityRepository = ActivityDaoRepository(),
    mediationRepository: MediationRepository = MediationDaoRepository(),
    mediations: Mediations = MediationsExposed(),
): MatchmakingsModule {
    val matchmakingService = MatchmakingService(
        matchmakingRepository,
        mediationRepository,
        activityRepository,
        matchmakingEventsPublisher
    )
    return MatchmakingsModule(
        matchmakingsController = MatchmakingsController(
            transactionalRunner,
            matchmakingRepository,
            matchmakingEventsPublisher
        ),
        matchmakingCreatedEventConsumer = MatchmakingCreatedEventConsumer(matchmakingService),
        mediationsController = MediationsController(mediations, fellows)

    )
}

class MatchmakingsModule(
    val matchmakingsController: MatchmakingsController,
    val mediationsController: MediationsController,
    val matchmakingCreatedEventConsumer: MatchmakingCreatedEventConsumer
)
