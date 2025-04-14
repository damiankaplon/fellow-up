package io.fellowup.domain.test.mediation

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.mediation.ActivityProposal
import io.fellowup.domain.mediation.Mediation
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import java.time.Instant
import kotlin.test.Test

internal class MediationTest {

    @Test
    fun `should set mediation as finished when all participants agree one of the locations`() {
        // given
        val participant1 = ParticipantId()
        val participant2 = ParticipantId()
        val participant3 = ParticipantId()
        val mediation = Mediation(category = "soccer", participants = setOf(participant1, participant2, participant3))
        mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T17:00:00Z"))
        mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T16:50:00Z"))
        mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T17:20:00Z"))

        // when
        mediation.accept(participant1, ActivityProposal.Order(1))
        mediation.accept(participant2, ActivityProposal.Order(1))
        mediation.accept(participant3, ActivityProposal.Order(1))

        // then
        assertThat(mediation.isFinished).isTrue()
    }

    @Test
    fun `should not allow to propose another activity given already finished mediation`() {
        // given
        val participant1 = ParticipantId()
        val participant2 = ParticipantId()
        val participant3 = ParticipantId()
        val mediation = Mediation(category = "soccer", participants = setOf(participant1, participant2, participant3))
        mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T17:00:00Z"))
        mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T16:50:00Z"))
        mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T17:20:00Z"))
        mediation.accept(participant1, ActivityProposal.Order(1))
        mediation.accept(participant2, ActivityProposal.Order(1))
        mediation.accept(participant3, ActivityProposal.Order(1))

        // when
        val result = catchException {
            mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T17:20:00Z"))
        }

        // then
        assertThat(result).isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `should not allow to accept multiple proposals`() {
        // given
        val participant1 = ParticipantId()
        val participant2 = ParticipantId()
        val participant3 = ParticipantId()
        val mediation = Mediation(category = "soccer", participants = setOf(participant1, participant2, participant3))
        mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T17:00:00Z"))
        mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T17:10:00Z"))

        // when
        mediation.accept(participant1, ActivityProposal.Order(1))
        mediation.accept(participant1, ActivityProposal.Order(2))

        // then
        val firstProposal = mediation.proposals.first { it.order == ActivityProposal.Order(1) }
        assertThat(firstProposal.acceptedByParticipantIds).hasSize(0)
        val secondProposal = mediation.proposals.first { it.order == ActivityProposal.Order(2) }
        assertThat(secondProposal.acceptedByParticipantIds).hasSize(1)
    }

    @Test
    fun `should not allow to accept any proposal given already finished mediation`() {
        // given
        val participant1 = ParticipantId()
        val participant2 = ParticipantId()
        val participant3 = ParticipantId()
        val mediation = Mediation(category = "soccer", participants = setOf(participant1, participant2, participant3))
        mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T17:00:00Z"))
        mediation.propose(Location(12.0, 10.0), Instant.parse("2025-04-14T17:10:00Z"))
        mediation.accept(participant1, ActivityProposal.Order(1))
        mediation.accept(participant2, ActivityProposal.Order(1))
        mediation.accept(participant3, ActivityProposal.Order(1))

        // when
        val result = catchException { mediation.accept(participant1, ActivityProposal.Order(2)) }

        // then
        assertThat(result).isInstanceOf(IllegalStateException::class.java)
    }
}