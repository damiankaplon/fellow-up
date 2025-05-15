package io.fellowup.infrastructure

import dagger.BindsInstance
import dagger.Component
import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.matchmaking.MatchmakingCreatedEventConsumer
import io.fellowup.infrastructure.db.dagger.DatabaseModule
import io.fellowup.infrastructure.kafka.KafkaModule
import io.fellowup.infrastructure.kafka.KafkaOutboxService
import io.fellowup.infrastructure.matchmaking.MatchmakingController
import io.fellowup.infrastructure.matchmaking.MatchmakingModule
import io.fellowup.infrastructure.mediation.MediationModule
import io.fellowup.infrastructure.mediation.readmodel.MediationsController
import io.fellowup.infrastructure.mediation.readmodel.keycloak.KeycloakDatabaseFellowsModule
import io.ktor.server.config.*
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Database

@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        KafkaModule::class,
        MatchmakingModule::class,
        KeycloakDatabaseFellowsModule::class,
        MediationModule::class
    ]
)
interface FellowUpAppComponent {
    fun database(): Database
    fun transactionalRunner(): TransactionalRunner
    fun kafkaOutboxService(): KafkaOutboxService
    fun matchmakingCreatedEventConsumer(): MatchmakingCreatedEventConsumer
    fun matchmakingController(): MatchmakingController
    fun mediationController(): MediationsController

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationConfig(config: ApplicationConfig): Builder
        fun build(): FellowUpAppComponent
    }
}
