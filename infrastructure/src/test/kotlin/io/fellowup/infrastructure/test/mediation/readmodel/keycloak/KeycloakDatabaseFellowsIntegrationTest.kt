package io.fellowup.infrastructure.test.mediation.readmodel.keycloak

import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.Fellow
import io.fellowup.infrastructure.db.ExposedTransactionalRunner
import io.fellowup.infrastructure.mediation.readmodel.keycloak.KeycloakDatabaseFellows
import io.fellowup.infrastructure.mediation.readmodel.keycloak.KeycloakDatabaseTransactionalRunner
import io.fellowup.infrastructure.test.DatabaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.insert
import java.util.*
import kotlin.test.Test

internal class KeycloakDatabaseFellowsIntegrationTest : DatabaseIntegrationTest() {

    private val keycloakDatabaseTransactionalRunner =
        KeycloakDatabaseTransactionalRunner(ExposedTransactionalRunner(super.keycloakDb))

    private val testee = KeycloakDatabaseFellows(keycloakDatabaseTransactionalRunner)

    @Test
    fun `should return fellow read model given keycloak database user of id`() = rollbackKeycloakTransaction {
        // given
        val user1Id = UUID.randomUUID()
        KeycloakUsersTable.insert {
            it[KeycloakUsersTable.id] = user1Id.toString()
            it[KeycloakUsersTable.firstName] = "Jane"
            it[KeycloakUsersTable.lastName] = "Doe"
        }

        val user2Id = UUID.randomUUID()
        KeycloakUsersTable.insert {
            it[KeycloakUsersTable.id] = user2Id.toString()
            it[KeycloakUsersTable.firstName] = "John"
            it[KeycloakUsersTable.lastName] = "Doe"
        }

        // when
        val result: Fellow = testee.findByParticipantId(ParticipantId(user1Id))

        // then
        assertThat(result.participantId).isEqualTo(user1Id.toString())
        assertThat(result.name).isEqualTo("Jane Doe")
    }

    @Test
    fun `should return fellow read models given keycloak database user of ids`() = rollbackKeycloakTransaction {
        // given
        val user1Id = UUID.randomUUID()
        KeycloakUsersTable.insert {
            it[KeycloakUsersTable.id] = user1Id.toString()
            it[KeycloakUsersTable.firstName] = "Jane"
            it[KeycloakUsersTable.lastName] = "Doe"
        }

        val user2Id = UUID.randomUUID()
        KeycloakUsersTable.insert {
            it[KeycloakUsersTable.id] = user2Id.toString()
            it[KeycloakUsersTable.firstName] = "John"
            it[KeycloakUsersTable.lastName] = "Doe"
        }

        val user3Id = UUID.randomUUID()
        KeycloakUsersTable.insert {
            it[KeycloakUsersTable.id] = user3Id.toString()
            it[KeycloakUsersTable.firstName] = "Max"
            it[KeycloakUsersTable.lastName] = "Counterman"
        }

        // when
        val result: Set<Fellow> = testee.findByParticipantIds(
            setOf(ParticipantId(user1Id), ParticipantId(user2Id))
        )

        // then
        assertThat(result).hasSize(2)
        assertThat(result).satisfiesExactlyInAnyOrder(
            {
                assertThat(it.participantId).isEqualTo(user1Id.toString())
                assertThat(it.name).isEqualTo("Jane Doe")
            },
            {
                assertThat(it.participantId).isEqualTo(user2Id.toString())
                assertThat(it.name).isEqualTo("John Doe")
            }
        )
    }
}
