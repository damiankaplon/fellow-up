package io.fellowup.infrastructure

import dagger.BindsInstance
import dagger.Component
import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.mediation.readmodel.Fellows
import io.fellowup.infrastructure.db.dagger.DatabaseModule
import io.fellowup.infrastructure.mediation.readmodel.keycloak.KeycloakDatabaseFellowsModule
import io.ktor.server.config.*
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Database

@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        KeycloakDatabaseFellowsModule::class
    ]
)
interface FellowUpAppComponent {
    fun database(): Database
    fun transactionalRunner(): TransactionalRunner
    fun fellows(): Fellows

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationConfig(config: ApplicationConfig): Builder
        fun build(): FellowUpAppComponent
    }
}
