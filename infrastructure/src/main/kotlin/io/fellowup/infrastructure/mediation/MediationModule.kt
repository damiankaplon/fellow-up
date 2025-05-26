package io.fellowup.infrastructure.mediation

import dagger.Module
import dagger.Provides
import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.events.Topic
import io.fellowup.domain.matchmaking.ActivityRepository
import io.fellowup.domain.mediation.MediationEvent
import io.fellowup.domain.mediation.MediationRepository
import io.fellowup.domain.mediation.readmodel.Fellows
import io.fellowup.domain.mediation.readmodel.MediationMatchmakings
import io.fellowup.domain.mediation.readmodel.Mediations
import io.fellowup.infrastructure.events.outbox.OutboxPublisher
import io.fellowup.infrastructure.matchmaking.ActivityDaoRepository
import io.fellowup.infrastructure.mediation.readmodel.MediationMatchmakingsExposed
import io.fellowup.infrastructure.mediation.readmodel.MediationStartedReadModelConsumer
import io.fellowup.infrastructure.mediation.readmodel.MediationsController
import io.fellowup.infrastructure.mediation.readmodel.MediationsExposed
import jakarta.inject.Singleton

@Module
class MediationModule {


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
    fun provideMediations(transactionalRunner: TransactionalRunner): Mediations {
        return MediationsExposed(transactionalRunner)
    }

    @Provides
    @Singleton
    fun provideMediationEventsPublisher(transactionalRunner: TransactionalRunner): EventPublisher<MediationEvent> {
        return OutboxPublisher(
            defaultTopic = Topic("io.fellowup.matchmaking.domain.mediation"),
            transactionalRunner = transactionalRunner
        )
    }

    @Provides
    @Singleton
    fun provideMediationMatchmakings(transactionalRunner: TransactionalRunner): MediationMatchmakings {
        return MediationMatchmakingsExposed(transactionalRunner)
    }

    @Provides
    @Singleton
    fun provideMediationStartedConsumer(mediationMatchmakings: MediationMatchmakings): MediationStartedReadModelConsumer {
        return MediationStartedReadModelConsumer(mediationMatchmakings)
    }

    @Provides
    @Singleton
    fun provideMediationsController(
        mediations: Mediations,
        mediationMatchmakings: MediationMatchmakings,
        fellows: Fellows,
    ): MediationsController {
        return MediationsController(mediations, mediationMatchmakings, fellows)
    }
}
