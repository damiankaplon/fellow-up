package io.fellowup.infrastructure.test.matchmaking

import dagger.Component
import dagger.Module
import dagger.Provides
import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.matchmaking.MatchmakingEvent
import io.fellowup.domain.matchmaking.MatchmakingRepository
import io.fellowup.domain.matchmaking.MatchmakingService
import io.fellowup.domain.mediation.MediationEvent
import io.fellowup.domain.test.fixtures.db.MockTransactionalRunner
import io.fellowup.domain.test.fixtures.events.NopEventPublisher
import io.fellowup.domain.test.fixtures.matchmaking.ActivityInMemoryRepository
import io.fellowup.domain.test.fixtures.matchmaking.MatchmakingInMemoryRepository
import io.fellowup.domain.test.fixtures.mediation.MediationInMemoryRepository
import io.fellowup.domain.test.fixtures.mediation.MediationMatchmakingsInMemory
import io.fellowup.infrastructure.installAppRouting
import io.fellowup.infrastructure.installSerialization
import io.fellowup.infrastructure.matchmaking.MatchmakingController
import io.fellowup.infrastructure.mediation.readmodel.MediationsController
import io.fellowup.infrastructure.test.MockJwtAuthenticationProvider
import io.fellowup.infrastructure.test.mediation.readmodel.FellowsInMemory
import io.fellowup.infrastructure.test.mediation.readmodel.MediationsInMemory
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import jakarta.inject.Singleton


@Module
internal class MatchmakingTestModule {

    @Provides
    @Singleton
    fun provideMatchmakingController(
        transactionalRunner: TransactionalRunner,
        matchmakingRepository: MatchmakingRepository,
        matchmakingEventsPublisher: EventPublisher<MatchmakingEvent>,
        mediationMatchmakings: MediationMatchmakingsInMemory,
    ): MatchmakingController = MatchmakingController(
        transactionalRunner,
        matchmakingRepository,
        mediationMatchmakings,
        matchmakingEventsPublisher,
    )

    @Provides
    @Singleton
    fun provideMatchmakingService(
        matchmakingRepository: MatchmakingRepository,
        mediationRepository: MediationInMemoryRepository,
        activityRepository: ActivityInMemoryRepository,
        matchmakingEventsPublisher: EventPublisher<MatchmakingEvent>,
        mediationEventsPublisher: EventPublisher<MediationEvent>
    ): MatchmakingService = MatchmakingService(
        matchmakingRepository,
        mediationRepository,
        activityRepository,
        matchmakingEventsPublisher,
        mediationEventsPublisher
    )

    @Provides
    @Singleton
    fun provideMockJwtAuthenticationProvider(): MockJwtAuthenticationProvider {
        return MockJwtAuthenticationProvider()
    }

    @Provides
    @Singleton
    fun provideTransactionRunner(): TransactionalRunner =
        MockTransactionalRunner()

    @Provides
    @Singleton
    fun provideMatchmakingEventsPublisher(): EventPublisher<MatchmakingEvent> = NopEventPublisher()

    @Provides
    @Singleton
    fun provideMediationEventsPublisher(): EventPublisher<MediationEvent> = NopEventPublisher()

    @Provides
    @Singleton
    fun provideMatchmakingRepository(): MatchmakingRepository = MatchmakingInMemoryRepository()

    @Provides
    @Singleton
    fun provideActivityRepository(): ActivityInMemoryRepository = ActivityInMemoryRepository()

    @Provides
    @Singleton
    fun provideMediationRepository(): MediationInMemoryRepository = MediationInMemoryRepository()

    @Provides
    @Singleton
    fun provideMediationMatchmakings(): MediationMatchmakingsInMemory = MediationMatchmakingsInMemory()

    @Provides
    @Singleton
    fun provideMediations(): MediationsInMemory = MediationsInMemory()

    @Provides
    @Singleton
    fun provideFellows(): FellowsInMemory = FellowsInMemory()

    @Provides
    @Singleton
    fun provideMediationsController(
        mediations: MediationsInMemory,
        mediationMatchmakings: MediationMatchmakingsInMemory,
        fellows: FellowsInMemory
    ): MediationsController {
        return MediationsController(mediations, mediationMatchmakings, fellows)
    }
}

@Singleton
@Component(
    modules = [MatchmakingTestModule::class]
)
internal interface MatchmakingTestAppComponent {
    fun mockJwtAuthenticationProvider(): MockJwtAuthenticationProvider
    fun transactionalRunner(): TransactionalRunner
    fun matchmakingEventsPublisher(): EventPublisher<MatchmakingEvent>
    fun matchmakingRepository(): MatchmakingRepository
    fun activityRepository(): ActivityInMemoryRepository
    fun mediationRepository(): MediationInMemoryRepository
    fun mediations(): MediationsInMemory
    fun fellows(): FellowsInMemory
    fun matchmakingService(): MatchmakingService
    fun matchmakingController(): MatchmakingController
    fun mediationsController(): MediationsController
    fun mediationMatchmakings(): MediationMatchmakingsInMemory
}

internal class MatchmakingTestApp(
    val component: MatchmakingTestAppComponent
)

internal fun ApplicationTestBuilder.setupMatchmakingTestApp(): MatchmakingTestApp {
    val matchmakingTestAppComponent: MatchmakingTestAppComponent = DaggerMatchmakingTestAppComponent.create()
    environment { config = ApplicationConfig("application-test.yaml") }
    createClient { install(ContentNegotiation) { json() } }
    application {
        installSerialization()
        install(Authentication) { register(matchmakingTestAppComponent.mockJwtAuthenticationProvider()) }
        routing {
            installAppRouting(
                matchmakingTestAppComponent.mockJwtAuthenticationProvider(),
                matchmakingTestAppComponent.matchmakingController(),
                matchmakingTestAppComponent.mediationsController()
            )
        }
    }
    return MatchmakingTestApp(matchmakingTestAppComponent)
}
