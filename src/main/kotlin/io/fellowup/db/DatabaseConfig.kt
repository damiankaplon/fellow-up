package io.fellowup.db

import io.ktor.server.application.*


data class DatabaseConfig(
    val jdbcUrl: String,
    val user: String,
    val password: String,
    val schema: String = "public",
)

fun interface DatabaseConfigProvider {
    @Throws(IllegalStateException::class)
    fun provide(): DatabaseConfig
}

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
