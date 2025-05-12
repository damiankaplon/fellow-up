package io.fellowup.infrastructure.db.dagger

import dagger.Module
import dagger.Provides
import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.infrastructure.db.ExposedTransactionalRunner
import io.fellowup.infrastructure.db.HikariCPDataSourceProvider
import io.fellowup.infrastructure.db.KtorEnvDatabaseConfigProvider
import io.ktor.server.config.*
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Database

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideExposedDatabase(ktorAppConfig: ApplicationConfig): Database {
        val databaseConfigProvider = KtorEnvDatabaseConfigProvider(ktorAppConfig)
        val databaseSourceProvider = HikariCPDataSourceProvider(databaseConfigProvider, ktorAppConfig)
        val database = Database.connect(databaseSourceProvider.provide())
        return database
    }

    @Provides
    @Singleton
    fun provideTransactionRunner(database: Database): TransactionalRunner {
        return ExposedTransactionalRunner(database)
    }
}
