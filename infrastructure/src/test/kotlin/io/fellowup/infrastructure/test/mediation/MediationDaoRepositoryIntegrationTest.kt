package io.fellowup.infrastructure.test.mediation

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.mediation.ActivityProposal
import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.infrastructure.mediation.MediationDaoRepository
import io.fellowup.infrastructure.test.DatabaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import java.time.Instant
import java.util.*
import kotlin.test.Test

internal class MediationDaoRepositoryIntegrationTest : DatabaseIntegrationTest() {

    private val testee: MediationDaoRepository = MediationDaoRepository()

    @Test
    fun `should persist all required data`() = rollbackTransaction {
        // given
        val participant1 = ParticipantId(UUID.randomUUID())
        val participant2 = ParticipantId(UUID.randomUUID())
        val mediation = Mediation(
            category = "soccer",
            participants = setOf(participant1, participant2)
        )
        mediation.propose(Location(10.0, 10.5), Instant.parse("2025-04-22T20:13:00Z"))
        mediation.accept(participant1, ActivityProposal.Order(1))
        mediation.accept(participant2, ActivityProposal.Order(1))

        // when
        testee.save(mediation)
        val result = testee.findById(mediation.id)

        // then
        assertThat(result).isNotNull
        assertThat(result!!.isFinished).isTrue()
        assertThat(result.category).isEqualTo(mediation.category)
        assertThat(result.id).isEqualTo(mediation.id)
        assertThat(result.proposals).singleElement().satisfies(
            { proposal -> assertThat(proposal.acceptedByParticipantIds).containsExactly(participant1, participant2) },
            { proposal -> assertThat(proposal.location).isEqualTo(Location(10.0, 10.5)) },
            { proposal -> assertThat(proposal.order).isEqualTo(ActivityProposal.Order(1)) },
        )
    }
}
