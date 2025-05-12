package io.fellowup.infrastructure.mediation.readmodel.keycloak

import dagger.Module
import dagger.Provides
import io.fellowup.domain.mediation.readmodel.Fellows
import io.ktor.server.config.*
import jakarta.inject.Singleton

@Module
class KeycloakDatabaseFellowsModule {

    @Provides
    @Singleton
    fun keycloakDatabaseTransactionalRunner(ktorAppConfig: ApplicationConfig): KeycloakDatabaseTransactionalRunner {
        return KeycloakDatabaseTransactionalRunner(ktorAppConfig)
    }

    @Provides
    @Singleton
    fun keycloakDatabaseFellows(keycloakDatabaseTransactionalRunner: KeycloakDatabaseTransactionalRunner): Fellows {
        return KeycloakDatabaseFellows(keycloakDatabaseTransactionalRunner)
    }
}
