package io.fellowup.infrastructure.mediation.readmodel.keycloak

import io.fellowup.infrastructure.db.ExposedTransactionalRunner
import io.fellowup.infrastructure.db.HikariCPDataSourceProvider
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database

fun KeycloakDatabaseTransactionalRunner(ktorAppConfig: ApplicationConfig): KeycloakDatabaseTransactionalRunner {
    val keycloakDatabaseConfigProvider = KtorEnvDatabaseConfigProvider(ktorAppConfig)
    val hikariCPDataSourceProvider = HikariCPDataSourceProvider(keycloakDatabaseConfigProvider, ktorAppConfig)
    val keycloakDatabase = Database.connect(hikariCPDataSourceProvider.provide())
    return KeycloakDatabaseTransactionalRunner(ExposedTransactionalRunner(keycloakDatabase))
}
