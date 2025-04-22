package io.fellowup.domain.matchmaking

import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.matchmaking.ActivityMatchSpecification.LocationSpecification
import io.fellowup.domain.matchmaking.ActivityMatchSpecification.TimeSpecification
import io.fellowup.domain.matchmaking.MatchmakingEvent.ActivityMatched
import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.MediationRepository
import io.fellowup.domain.mediation.ParticipantId

private const val TEN_KM_IN_METERS: Int = 10000
private const val TWO_HOURS_IN_SECONDS: Int = 120 * 60

class MatchmakingService(
    private val matchmakingRepository: MatchmakingRepository,
    private val mediationRepository: MediationRepository,
    private val activityRepository: ActivityRepository,
    private val distanceCalculator: DistanceCalculator,
    private val eventPublisher: EventPublisher<MatchmakingEvent>
) {
    suspend fun match(matchmakingId: Matchmaking.Id) {
        val matchmaking: Matchmaking = matchmakingRepository.findByIdOrThrow(matchmakingId)
        val activities: Set<Activity> = activityRepository.findDistanceWithinAndTimeDiffWithin(
            matchmaking.location,
            TEN_KM_IN_METERS,
            matchmaking.at,
            TWO_HOURS_IN_SECONDS
        )
        val matchedActivities = activities.filter { activity ->
            LocationSpecification(distanceCalculator, TEN_KM_IN_METERS).isMatching(matchmaking, activity) &&
                    TimeSpecification(TWO_HOURS_IN_SECONDS).isMatching(matchmaking, activity)
        }

        if (matchedActivities.isNotEmpty()) {
            matchedActivities.forEach { activity ->
                eventPublisher.publish(ActivityMatched(matchmaking.id, activity.id))
            }
            return
        }

        val similarMatchmakings = matchmakingRepository.findDistanceWithinAndTimeDiffWithinAndCategory(
            matchmaking.category,
            matchmaking.location,
            TEN_KM_IN_METERS,
            matchmaking.at,
            TWO_HOURS_IN_SECONDS
        )
        if (similarMatchmakings.size < 3) return
        val mediationMatchmakings = similarMatchmakings.take(3)
        val mediation = Mediation(
            category = matchmaking.category,
            participants = mediationMatchmakings.map { ParticipantId(it.userId) }.toSet()
        )
        mediationMatchmakings.forEach { mediation.propose(it.location, it.at) }
        mediationRepository.save(mediation)
    }
}
