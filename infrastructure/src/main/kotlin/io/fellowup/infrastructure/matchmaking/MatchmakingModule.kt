package io.fellowup.infrastructure.matchmaking

import dagger.Module
import dagger.Provides
import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.events.Topic
import io.fellowup.domain.matchmaking.*
import io.fellowup.domain.mediation.MediationRepository
import io.fellowup.domain.mediation.readmodel.Mediations
import io.fellowup.infrastructure.events.outbox.OutboxPublisher
import io.fellowup.infrastructure.mediation.MediationDaoRepository
import io.fellowup.infrastructure.mediation.readmodel.MediationsExposed
import jakarta.inject.Singleton

@Module
class MatchmakingModule {

    @Provides
    @Singleton
    fun provideMatchmakingController(
        transactionalRunner: TransactionalRunner,
        matchmakingRepository: MatchmakingRepository,
        matchmakingEventsPublisher: EventPublisher<MatchmakingEvent>
    ): MatchmakingController {
        return MatchmakingController(
            transactionalRunner,
            matchmakingRepository,
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
    fun provideActivityRepository(): ActivityRepository {
        return ActivityDaoRepository()
    }

    @Provides
    @Singleton
    fun provideMediationRepository(): MediationRepository {
        return MediationDaoRepository()
    }


    @Provides
    @Singleton
    fun provideMediations(): Mediations {
        return MediationsExposed()
    }

    @Provides
    @Singleton
    fun provideMatchmakingService(
        matchmakingRepository: MatchmakingRepository,
        mediationRepository: MediationRepository,
        activityRepository: ActivityRepository,
        matchmakingEventsPublisher: EventPublisher<MatchmakingEvent>
    ): MatchmakingService {
        return MatchmakingService(
            matchmakingRepository,
            mediationRepository,
            activityRepository,
            matchmakingEventsPublisher
        )
    }
}