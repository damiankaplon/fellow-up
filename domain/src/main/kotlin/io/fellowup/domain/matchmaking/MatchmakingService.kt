package io.fellowup.domain.matchmaking

import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.matchmaking.MatchmakingEvent.ActivityMatched
import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.MediationEvent
import io.fellowup.domain.mediation.MediationEvent.MediationStarted
import io.fellowup.domain.mediation.MediationRepository
import java.util.*

private const val TEN_KM_IN_METERS: Int = 10000
private const val TWO_HOURS_IN_SECONDS: Int = 120 * 60

class MatchmakingService(
    private val matchmakingRepository: MatchmakingRepository,
    private val mediationRepository: MediationRepository,
    private val activityRepository: ActivityRepository,
    private val matchmakingEventsPublisher: EventPublisher<MatchmakingEvent>,
    private val mediationEventsPublisher: EventPublisher<MediationEvent>
) {
    suspend fun match(matchmakingId: Matchmaking.Id) {
        val matchmaking: Matchmaking = matchmakingRepository.findByIdOrThrow(matchmakingId)
        val matchedActivities: Set<Activity> = activityRepository.findMatchingTo(
            matchmaking.location,
            TEN_KM_IN_METERS,
            matchmaking.at,
            TWO_HOURS_IN_SECONDS
        )

        if (matchedActivities.isNotEmpty()) {
            matchedActivities.forEach { activity ->
                matchmakingEventsPublisher.publish(ActivityMatched(matchmaking.id, activity.id))
            }
            return
        }

        val similarMatchmakings = matchmakingRepository.findMatchingTo(
            category = matchmaking.category,
            location = matchmaking.location,
            maxMetersDiff = TEN_KM_IN_METERS,
            time = matchmaking.at,
            maxSecondsDiff = TWO_HOURS_IN_SECONDS
        )
        val groupedSimilarMatchmakings: Map<UUID, List<Matchmaking>> = similarMatchmakings.groupBy { it.userId }

        if (groupedSimilarMatchmakings.keys.size < 3) return
        val mediationMatchmakings = groupedSimilarMatchmakings.keys.take(3).map { userId: UUID ->
            groupedSimilarMatchmakings.getValue(userId).first()
        }.toSet()
        val mediation = Mediation(mediationMatchmakings)
        mediationRepository.save(mediation)
        MediationStarted(
            mediationId = mediation.id,
            includedMatchmakings = mediationMatchmakings.map(Matchmaking::id).toSet()
        ).run { mediationEventsPublisher.publish(this) }
    }
}
