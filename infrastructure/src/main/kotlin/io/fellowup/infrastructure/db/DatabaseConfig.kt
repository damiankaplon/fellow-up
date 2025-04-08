package io.fellowup.infrastructure.db

import io.fellowup.domain.db.DatabaseConfig
import io.fellowup.domain.db.DatabaseConfigProvider
import io.ktor.server.application.*

class KtorEnvDatabaseConfigProvider(
    private val env: ApplicationEnvironment
) : DatabaseConfigProvider {
    override fun provide(): DatabaseConfig =
        DatabaseConfig(
            jdbcUrl = env.config.property("db.jdbcUrl").getString(),
            user = env.config.property("db.user").getString(),
            password = env.config.property("db.password").getString(),
            schema = env.config.property("db.schema").getString(),
        )
}
