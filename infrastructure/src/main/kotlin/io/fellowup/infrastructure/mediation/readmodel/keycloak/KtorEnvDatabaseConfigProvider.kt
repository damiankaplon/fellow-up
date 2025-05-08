package io.fellowup.infrastructure.mediation.readmodel.keycloak

import io.fellowup.domain.db.DatabaseConfig
import io.fellowup.domain.db.DatabaseConfigProvider
import io.ktor.server.application.*

class KtorEnvDatabaseConfigProvider(
    private val env: ApplicationEnvironment
) : DatabaseConfigProvider {
    override fun provide(): DatabaseConfig = DatabaseConfig(
        jdbcUrl = env.config.property("db.keycloak.jdbcUrl").getString(),
        user = env.config.property("db.keycloak.user").getString(),
        password = env.config.property("db.keycloak.password").getString(),
        schema = env.config.property("db.keycloak.schema").getString(),
    )
}
