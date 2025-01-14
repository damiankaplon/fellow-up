package io.fellowup.db

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

class DatabaseModule(
    val transactionManager: TransactionalRunner,
)

fun Application.installDatabaseModule(): DatabaseModule {
    val databaseConfigProvider = KtorEnvDatabaseConfigProvider(environment)
    val databaseSourceProvider = HikariCPDataSourceProvider(databaseConfigProvider, environment)
    val database = Database.connect(databaseSourceProvider.provide())
    val transactionalRunner = ExposedTransactionalRunner(database)
    return DatabaseModule(transactionalRunner)
}