package io.fellowup.infrastructure.mediation.readmodel.keycloak

import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.Fellow
import io.fellowup.domain.mediation.readmodel.Fellows
import org.jetbrains.exposed.sql.Table
import java.util.*

class KeycloakDatabaseFellows(
    private val keycloakDatabaseTransactionalRunner: KeycloakDatabaseTransactionalRunner
) : Fellows {

    override suspend fun findByParticipantId(participantId: ParticipantId): Fellow {
        return keycloakDatabaseTransactionalRunner.readOnlyTransaction {
            Users.select(Users.id, Users.firstName, Users.lastName)
                .where { Users.id eq participantId.id.toString() }
                .single()
                .let {
                    object : Fellow {
                        override val participantId: String = it[Users.id]
                        override val name: String = "${it[Users.firstName]} ${it[Users.lastName]}"
                    }
                }
        }

    }

    override suspend fun findByParticipantIds(participantIds: Set<ParticipantId>): Set<Fellow> {
        return keycloakDatabaseTransactionalRunner.readOnlyTransaction {
            Users.select(Users.id, Users.firstName, Users.lastName)
                .where { Users.id inList participantIds.map(ParticipantId::id).map(UUID::toString) }
                .mapTo(linkedSetOf()) {
                    object : Fellow {
                        override val participantId: String = it[Users.id]
                        override val name: String = "${it[Users.firstName]} ${it[Users.lastName]}"
                    }
                }
        }
    }
}

private object Users : Table("user_entity") {
    val id = varchar("id", 36)
    val firstName = varchar("first_name", 255).nullable()
    val lastName = varchar("last_name", 255).nullable()
}
