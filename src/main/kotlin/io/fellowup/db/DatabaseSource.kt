package io.fellowup.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import javax.sql.DataSource

fun interface DataSourceProvider {
    @Throws(IllegalStateException::class)
    fun provide(): DataSource
}

class HikariCPDataSourceProvider(
    private val databaseConfigProvider: DatabaseConfigProvider,
    private val env: ApplicationEnvironment
) : DataSourceProvider {
    override fun provide(): DataSource {
        val dbConfig = databaseConfigProvider.provide()
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbConfig.jdbcUrl
            username = dbConfig.user
            password = dbConfig.password
            schema = dbConfig.schema
            maximumPoolSize = env.config.propertyOrNull("db.poolSize")?.getString()?.toInt() ?: 10
        }
        return HikariDataSource(hikariConfig)
    }
}
