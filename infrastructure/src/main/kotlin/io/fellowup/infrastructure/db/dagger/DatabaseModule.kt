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
import javax.sql.DataSource

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDataSource(ktorAppConfig: ApplicationConfig): DataSource {
        val databaseConfigProvider = KtorEnvDatabaseConfigProvider(ktorAppConfig)
        return HikariCPDataSourceProvider(databaseConfigProvider, ktorAppConfig).provide()
    }

    @Provides
    @Singleton
    fun provideExposedDatabase(dataSource: DataSource): Database {
        val database = Database.connect(dataSource)
        return database
    }

    @Provides
    @Singleton
    fun provideTransactionRunner(database: Database): TransactionalRunner {
        return ExposedTransactionalRunner(database)
    }
}
