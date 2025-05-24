package io.fellowup.infrastructure.test.mediation.readmodel

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.mediation.ActivityProposal
import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.test.fixtures.utcInstant
import io.fellowup.infrastructure.mediation.MediationDaoRepository
import io.fellowup.infrastructure.mediation.readmodel.MediationsExposed
import io.fellowup.infrastructure.test.DatabaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import java.util.*
import kotlin.test.Test
import io.fellowup.domain.mediation.readmodel.Mediation as MediationReadModel

internal class MediationsIntegrationTest : DatabaseIntegrationTest() {

    private val mediationRepository = MediationDaoRepository()
    private val mediations = MediationsExposed()

    @Test
    fun `should return mediation read model containing proposals`() = rollbackTransaction {
        // given
        val participant1 = ParticipantId(UUID.randomUUID())
        val participant2 = ParticipantId(UUID.randomUUID())
        val mediation = Mediation(
            category = "soccer",
            participants = setOf(participant1, participant2)
        )
        mediation.propose(Location(10.0, 10.5), "2025-04-22T20:13:00".utcInstant())
        mediation.accept(participant1, ActivityProposal.Order(1))
        mediationRepository.save(mediation)

        // when
        val result = mediations.findNotFinishedByParticipant(participant1)

        // then
        assertThat(result).singleElement().satisfies(
            { mediation ->
                assertThat(mediation.category).isEqualTo("soccer")
                assertThat(mediation.participantIds).contains(participant1, participant2)
                assertThat(mediation.proposals).singleElement().satisfies(
                    {
                        assertThat(it.location).isEqualTo(Location(10.0, 10.5))
                        assertThat(it.acceptedBy).isEqualTo(1)
                    }
                )
            }
        )
    }

    @Test
    fun `should filter out finished mediations`() = rollbackTransaction {
        // given
        val participant1 = ParticipantId(UUID.randomUUID())
        val participant2 = ParticipantId(UUID.randomUUID())
        val mediation1 = Mediation(
            category = "soccer",
            participants = setOf(participant1, participant2)
        )
        mediation1.propose(Location(10.0, 10.5), "2025-04-22T20:13:00".utcInstant())
        mediation1.accept(participant1, ActivityProposal.Order(1))
        mediation1.accept(participant2, ActivityProposal.Order(1))
        mediationRepository.save(mediation1)

        val mediation2 = Mediation(
            category = "soccer",
            participants = setOf(participant1, participant2)
        )
        mediationRepository.save(mediation2)

        // when
        val result = mediations.findNotFinishedByParticipant(participant1)

        // then
        assertThat(result.map { it.id }).doesNotContain(mediation1.id.value)
        assertThat(result).singleElement().satisfies(
            { mediation ->
                assertThat(mediation.category).isEqualTo("soccer")
                assertThat(mediation.participantIds).contains(participant2)
            }
        )
    }

    @Test
    fun `should filter out mediations not having given participant`() = rollbackTransaction {
        // given
        val participant1 = ParticipantId(UUID.randomUUID())
        val participant2 = ParticipantId(UUID.randomUUID())
        val mediation = Mediation(
            category = "soccer",
            participants = setOf(participant1, participant2)
        )
        mediationRepository.save(mediation)

        val participant3 = ParticipantId(UUID.randomUUID())
        val mediation2 = Mediation(
            category = "soccer",
            participants = setOf(participant3)
        )

        // when
        val result = mediations.findNotFinishedByParticipant(participant1)

        // then
        assertThat(result.map { it.id }).doesNotContain(mediation2.id.value)
        assertThat(result).singleElement().satisfies(
            { mediation ->
                assertThat(mediation.category).isEqualTo("soccer")
                assertThat(mediation.participantIds).contains(participant2)
            }
        )
    }

    @Test
    fun `should find mediation by mediation id`() = rollbackTransaction {
        // given
        val participant1 = ParticipantId(UUID.randomUUID())
        val participant2 = ParticipantId(UUID.randomUUID())
        val mediation = Mediation(
            category = "soccer",
            participants = setOf(participant1, participant2)
        )
        mediationRepository.save(mediation)

        // when
        val result: MediationReadModel? = mediations.findById(mediation.id)

        // then
        assertThat(result).isNotNull
        assertThat(result!!.id).isEqualTo(mediation.id.value)
        assertThat(result.participantIds).containsAll(setOf(participant1, participant2))
    }
}
