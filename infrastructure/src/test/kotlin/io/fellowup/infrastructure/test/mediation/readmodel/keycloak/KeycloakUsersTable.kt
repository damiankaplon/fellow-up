package io.fellowup.infrastructure.test.mediation.readmodel.keycloak

import org.jetbrains.exposed.sql.Table

internal object KeycloakUsersTable : Table("user_entity") {
    val id = varchar("id", 36)
    val firstName = varchar("first_name", 255).nullable()
    val lastName = varchar("last_name", 255).nullable()
}
