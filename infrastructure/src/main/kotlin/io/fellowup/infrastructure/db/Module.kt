package io.fellowup.infrastructure.db

import io.fellowup.domain.db.TransactionalRunner
import io.ktor.server.application.*
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
