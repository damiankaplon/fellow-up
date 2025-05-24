package io.fellowup.domain.test.matchmaking

import io.fellowup.domain.matchmaking.*
import io.fellowup.domain.mediation.MediationEvent
import io.fellowup.domain.test.fixtures.events.EventStoringPublisher
import io.fellowup.domain.test.fixtures.matchmaking.ActivityInMemoryRepository
import io.fellowup.domain.test.fixtures.matchmaking.MatchmakingInMemoryRepository
import io.fellowup.domain.test.fixtures.mediation.MediationInMemoryRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.time.Instant.parse
import java.util.*
import kotlin.test.Test

internal class MatchmakingTest {

    private val matchmakingRepository = MatchmakingInMemoryRepository()
    private val mediationRepository = MediationInMemoryRepository()
    private val activityRepository = ActivityInMemoryRepository()
    private val matchmakingEventsPublisher: EventStoringPublisher<MatchmakingEvent> = EventStoringPublisher()
    private val mediationEventsPublisher: EventStoringPublisher<MediationEvent> = EventStoringPublisher()
    private val matchmakingService: MatchmakingService =
        MatchmakingService(
            matchmakingRepository,
            mediationRepository,
            activityRepository,
            matchmakingEventsPublisher,
            mediationEventsPublisher
        )

    @Test
    fun `should match with activities within range of distance and time given matchmaking`(): Unit = runBlocking {
        // given
        // activity out of distance reach
        Activity(
            category = "soccer",
            at = parse("2025-04-14T11:00:00Z"),
            location = Location(51.623659, -3.925903)
        ).run { activityRepository.save(this) }
        // activity out of time reach
        Activity(
            category = "soccer",
            at = parse("2025-04-14T17:00:00Z"),
            location = Location(51.685951, -4.209131)
        ).run { activityRepository.save(this) }
        val matchingActivity1 = Activity(
            category = "soccer",
            at = parse("2025-04-14T11:15:00Z"),
            location = Location(51.685713, -4.207430)
        ).run { activityRepository.save(this) }
        val matchingActivity2 = Activity(
            category = "soccer",
            at = parse("2025-04-14T11:30:00Z"),
            location = Location(51.688114, -4.205616)
        ).run { activityRepository.save(this) }

        val matchmaking = Matchmaking(
            category = "soccer",
            userId = UUID.randomUUID(),
            at = parse("2025-04-14T11:00:00Z"),
            location = Location(51.685713, -4.206430)
        ).run { matchmakingRepository.save(this) }

        // when
        matchmakingService.match(matchmakingId = matchmaking.id)

        // then
        assertThat(matchmakingEventsPublisher.events).satisfiesExactlyInAnyOrder(
            {
                assertThat(it).isInstanceOf(MatchmakingEvent.ActivityMatched::class.java)
                assertThat((it as MatchmakingEvent.ActivityMatched).activityId).isEqualTo(matchingActivity1.id)
                assertThat(it.matchmakingId).isEqualTo(matchmaking.id)
            },
            {
                assertThat(it).isInstanceOf(MatchmakingEvent.ActivityMatched::class.java)
                assertThat((it as MatchmakingEvent.ActivityMatched).activityId).isEqualTo(matchingActivity2.id)
                assertThat(it.matchmakingId).isEqualTo(matchmaking.id)
            }
        )
    }

    @Test
    fun `should start mediation given no matching activities but 3 similar matchmakings of different users ongoing`(): Unit =
        runBlocking {
            // given
            val matchmaking1 = Matchmaking(
                category = "soccer",
                userId = UUID.randomUUID(),
                at = parse("2025-04-14T11:00:00Z"),
                location = Location(51.685713, -4.206430)
            ).run { matchmakingRepository.save(this) }

            val matchmaking2 = Matchmaking(
                category = "soccer",
                userId = UUID.randomUUID(),
                at = parse("2025-04-14T11:05:00Z"),
                location = Location(51.685800, -4.206500)
            ).run { matchmakingRepository.save(this) }

            val matchmaking3 = Matchmaking(
                category = "soccer",
                userId = UUID.randomUUID(),
                at = parse("2025-04-14T11:00:00Z"),
                location = Location(51.685713, -4.206430)
            ).run { matchmakingRepository.save(this) }

            // when
            matchmakingService.match(matchmakingId = matchmaking3.id)

            // then
            assertThat(mediationRepository.mediations).hasSize(1)
            val savedMediation = mediationRepository.mediations.first()
            assertThat(savedMediation).isNotNull
            assertThat(savedMediation.proposals.size).isEqualTo(3)
            assertThat(mediationEventsPublisher.events).satisfiesOnlyOnce {
                assertThat(it).isInstanceOf(MediationEvent.MediationStarted::class.java)
                val event = it as MediationEvent.MediationStarted
                assertThat(event.mediationId).isEqualTo(savedMediation.id)
                assertThat(event.includedMatchmakings).containsExactly(
                    matchmaking1.id,
                    matchmaking2.id,
                    matchmaking3.id
                )
            }
        }

    @Test
    fun `should do not start mediation given 3 matching matchmakings of the same user`(): Unit = runBlocking {
        // given
        val user = UUID.randomUUID()
        Matchmaking(
            category = "soccer",
            userId = user,
            at = parse("2025-04-14T11:00:00Z"),
            location = Location(51.685713, -4.206430)
        ).run { matchmakingRepository.save(this) }

        Matchmaking(
            category = "soccer",
            userId = user,
            at = parse("2025-04-14T11:05:00Z"),
            location = Location(51.685800, -4.206500)
        ).run { matchmakingRepository.save(this) }

        Matchmaking(
            category = "soccer",
            userId = user,
            at = parse("2025-04-14T11:10:00Z"),
            location = Location(51.685900, -4.206700)
        ).run { matchmakingRepository.save(this) }

        val matchmakingRequest = Matchmaking(
            category = "soccer",
            userId = user,
            at = parse("2025-04-14T11:00:00Z"),
            location = Location(51.685713, -4.206430)
        ).run { matchmakingRepository.save(this) }

        // when
        matchmakingService.match(matchmakingId = matchmakingRequest.id)

        // then
        assertThat(mediationRepository.mediations).hasSize(0)
    }
}
