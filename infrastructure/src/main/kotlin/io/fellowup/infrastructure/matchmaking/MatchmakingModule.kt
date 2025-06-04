package io.fellowup.infrastructure.matchmaking

import dagger.Module
import dagger.Provides
import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.events.Topic
import io.fellowup.domain.matchmaking.*
import io.fellowup.domain.mediation.MediationEvent
import io.fellowup.domain.mediation.MediationRepository
import io.fellowup.domain.mediation.readmodel.MediationMatchmakings
import io.fellowup.infrastructure.events.outbox.OutboxPublisher
import jakarta.inject.Singleton

@Module
class MatchmakingModule {

    @Provides
    @Singleton
    fun provideMatchmakingController(
        transactionalRunner: TransactionalRunner,
        matchmakingRepository: MatchmakingRepository,
        mediationMatchmakings: MediationMatchmakings,
        matchmakingEventsPublisher: EventPublisher<MatchmakingEvent>
    ): MatchmakingController {
        return MatchmakingController(
            transactionalRunner,
            matchmakingRepository,
            mediationMatchmakings,
            matchmakingEventsPublisher
        )
    }

    @Provides
    @Singleton
    fun provideMatchmakingEventsPublisher(transactionalRunner: TransactionalRunner): EventPublisher<MatchmakingEvent> {
        return OutboxPublisher(
            defaultTopic = Topic("io.fellowup.matchmaking.domain.matchmakingCreated"),
            transactionalRunner = transactionalRunner
        )
    }

    @Provides
    @Singleton
    fun provideMatchmakingCreatedEventConsumer(matchmakingService: MatchmakingService): MatchmakingCreatedEventConsumer {
        return MatchmakingCreatedEventConsumer(matchmakingService)
    }

    @Provides
    @Singleton
    fun provideMatchmakingRepository(): MatchmakingRepository {
        return MatchmakingDaoRepository()
    }

    @Provides
    @Singleton
    fun provideMatchmakingService(
        matchmakingRepository: MatchmakingRepository,
        mediationRepository: MediationRepository,
        activityRepository: ActivityRepository,
        matchmakingEventsPublisher: EventPublisher<MatchmakingEvent>,
        mediationEventsPublisher: EventPublisher<MediationEvent>
    ): MatchmakingService {
        return MatchmakingService(
            matchmakingRepository,
            mediationRepository,
            activityRepository,
            matchmakingEventsPublisher,
            mediationEventsPublisher
        )
    }
}
