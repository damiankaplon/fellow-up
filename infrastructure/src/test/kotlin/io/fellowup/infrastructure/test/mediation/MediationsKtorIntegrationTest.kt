package io.fellowup.infrastructure.test.mediation

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.Fellow
import io.fellowup.domain.mediation.readmodel.Mediation
import io.fellowup.domain.mediation.readmodel.Proposal
import io.fellowup.infrastructure.mediation.readmodel.MediationsController
import io.fellowup.infrastructure.test.clientJson
import io.fellowup.infrastructure.test.matchmaking.setupMatchmakingTestApp
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.assertj.core.api.Assertions.assertThat
import java.util.*
import kotlin.test.Test

class MediationsKtorIntegrationTest {

    @Test
    fun `should return mediations read model`() = testApplication {
        // given
        val testApp = setupMatchmakingTestApp()
        val participant1 = ParticipantId(UUID.randomUUID())
        val participant2 = ParticipantId(UUID.randomUUID())

        object : Fellow {
            override val participantId: String = participant1.id.toString()
            override val name: String = "John Doe"
        }.run { testApp.component.fellows().add(this) }

        object : Fellow {
            override val participantId: String = participant2.id.toString()
            override val name: String = "Jane Doe"
        }.run { testApp.component.fellows().add(this) }

        object : Mediation {
            override val id: UUID = UUID.randomUUID()
            override val category: String = "SOCCER"
            override val participantIds: Set<ParticipantId> = setOf(participant1, participant2)
            override val proposals: Set<Proposal> = setOf(
                object : Proposal {
                    override val acceptedBy: Int = 1
                    override val location: Location = Location(1.0, 2.0)
                },
                object : Proposal {
                    override val acceptedBy: Int = 2
                    override val location: Location = Location(2.0, 2.0)
                },
            )
        }.run { testApp.component.mediations().add(this) }

        testApp.component.mockJwtAuthenticationProvider().setTestJwtPrincipalSubject(participant1.id.toString())

        // when
        val result = clientJson.get("/api/mediations") {
            contentType(ContentType.Application.Json)
        }

        // then
        assertThat(result.status.value).isEqualTo(200)
        val body = result.body<Set<MediationsController.MediationDto>>()
        assertThat(body).hasSize(1)
        val mediationDto: MediationsController.MediationDto = body.single()
        assertThat(mediationDto.category).isEqualTo("SOCCER")
        assertThat(mediationDto.fellows).satisfiesExactlyInAnyOrder(
            {
                assertThat(it.id).isEqualTo(participant1.id.toString())
                assertThat(it.name).isEqualTo("John Doe")
            },
            {
                assertThat(it.id).isEqualTo(participant2.id.toString())
                assertThat(it.name).isEqualTo("Jane Doe")
            }
        )
        assertThat(mediationDto.proposals).hasSize(2)
        assertThat(mediationDto.proposals).satisfiesExactlyInAnyOrder(
            {
                assertThat(it.location).isEqualTo(MediationsController.LocationDto(2.0, 1.0))
                assertThat(it.acceptedBy).isEqualTo(1)
            },
            {
                assertThat(it.location).isEqualTo(MediationsController.LocationDto(2.0, 2.0))
                assertThat(it.acceptedBy).isEqualTo(2)
            }
        )
    }
}
