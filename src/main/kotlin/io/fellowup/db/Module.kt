package io.fellowup.db

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database


fun Application.installDatabase(): Database {
    val databaseConfigProvider = KtorEnvDatabaseConfigProvider(environment)
    val databaseSourceProvider = HikariCPDataSourceProvider(databaseConfigProvider, environment)
    val database = Database.connect(databaseSourceProvider.provide())
    return database
}

fun createTransactionalRunner(database: Database): TransactionalRunner {
    return ExposedTransactionalRunner(database)
}
