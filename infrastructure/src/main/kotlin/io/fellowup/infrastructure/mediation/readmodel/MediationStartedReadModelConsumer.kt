package io.fellowup.infrastructure.mediation.readmodel

import io.fellowup.domain.events.EventConsumer
import io.fellowup.domain.mediation.MediationEvent
import io.fellowup.domain.mediation.readmodel.MediationMatchmakings

class MediationStartedReadModelConsumer(
    private val mediationMatchmakings: MediationMatchmakings
) : EventConsumer<MediationEvent.MediationStarted> {

    override suspend fun handle(event: MediationEvent.MediationStarted) {
        mediationMatchmakings.save(event.mediationId, event.includedMatchmakings)
    }
}
