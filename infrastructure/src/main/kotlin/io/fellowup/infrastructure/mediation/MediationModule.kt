package io.fellowup.infrastructure.mediation

import dagger.Module
import dagger.Provides
import io.fellowup.domain.matchmaking.ActivityRepository
import io.fellowup.domain.mediation.MediationRepository
import io.fellowup.domain.mediation.readmodel.Fellows
import io.fellowup.domain.mediation.readmodel.Mediations
import io.fellowup.infrastructure.matchmaking.ActivityDaoRepository
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
    fun provideMediations(): Mediations {
        return MediationsExposed()
    }

    @Provides
    @Singleton
    fun provideMediationsController(
        mediations: Mediations,
        fellows: Fellows,
    ): MediationsController {
        return MediationsController(mediations, fellows)
    }
}
