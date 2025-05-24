package io.fellowup.infrastructure.test.mediation.readmodel

import io.fellowup.domain.matchmaking.Matchmaking
import io.fellowup.domain.mediation.Mediation
import io.fellowup.infrastructure.mediation.readmodel.MediationMatchmakingsExposed
import io.fellowup.infrastructure.test.DatabaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

internal class MediationMatchmakingsExposedIntegrationTest : DatabaseIntegrationTest() {

    private val mediationMatchmakings = MediationMatchmakingsExposed()

    @Test
    fun `should find mediation containing given matchmaking id`() = rollbackTransaction {
        // given
        val mediationId = Mediation.Id()
        val matchmakingId1 = Matchmaking.Id()
        val matchmakingId2 = Matchmaking.Id()
        mediationMatchmakings.save(mediationId, setOf(matchmakingId1, matchmakingId2))

        // when
        val result: Mediation.Id = mediationMatchmakings.findMediationOrThrow(matchmakingId1)

        // then
        assertThat(result).isEqualTo(mediationId)
    }
}
