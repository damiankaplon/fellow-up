package io.fellowup.infrastructure.mediation.readmodel.keycloak

import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.infrastructure.db.ExposedTransactionalRunner
import io.fellowup.infrastructure.db.HikariCPDataSourceProvider
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database

open class KeycloakDatabaseTransactionalRunner(
    private val exposedTransactionalRunner: TransactionalRunner
) {

    suspend fun <T> readOnlyTransaction(block: suspend () -> T): T {
        return exposedTransactionalRunner.transaction(readOnly = true, block = block)
    }
}

fun KeycloakDatabaseTransactionalRunner(ktorAppConfig: ApplicationConfig): KeycloakDatabaseTransactionalRunner {
    val keycloakDatabaseConfigProvider = KtorEnvDatabaseConfigProvider(ktorAppConfig)
    val hikariCPDataSourceProvider = HikariCPDataSourceProvider(keycloakDatabaseConfigProvider, ktorAppConfig)
    val keycloakDatabase = Database.connect(hikariCPDataSourceProvider.provide())
    return KeycloakDatabaseTransactionalRunner(ExposedTransactionalRunner(keycloakDatabase))
}
