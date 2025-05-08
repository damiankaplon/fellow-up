package io.fellowup.infrastructure.mediation.readmodel.keycloak

import io.fellowup.infrastructure.db.ExposedTransactionalRunner
import io.fellowup.infrastructure.db.HikariCPDataSourceProvider
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun KeycloakDatabaseTransactionalRunner(env: ApplicationEnvironment): KeycloakDatabaseTransactionalRunner {
    val keycloakDatabaseConfigProvider = KtorEnvDatabaseConfigProvider(env)
    val hikariCPDataSourceProvider = HikariCPDataSourceProvider(keycloakDatabaseConfigProvider, env)
    val keycloakDatabase = Database.connect(hikariCPDataSourceProvider.provide())
    return KeycloakDatabaseTransactionalRunner(ExposedTransactionalRunner(keycloakDatabase))
}
