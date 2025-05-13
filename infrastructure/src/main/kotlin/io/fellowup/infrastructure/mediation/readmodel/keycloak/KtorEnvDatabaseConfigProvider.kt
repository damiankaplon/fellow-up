package io.fellowup.infrastructure.mediation.readmodel.keycloak

import io.fellowup.domain.db.DatabaseConfig
import io.fellowup.domain.db.DatabaseConfigProvider
import io.ktor.server.config.*

class KtorEnvDatabaseConfigProvider(
    private val ktorAppConfig: ApplicationConfig
) : DatabaseConfigProvider {
    override fun provide(): DatabaseConfig = DatabaseConfig(
        jdbcUrl = ktorAppConfig.property("db.keycloak.jdbcUrl").getString(),
        user = ktorAppConfig.property("db.keycloak.user").getString(),
        password = ktorAppConfig.property("db.keycloak.password").getString(),
        schema = ktorAppConfig.property("db.keycloak.schema").getString(),
    )
}
