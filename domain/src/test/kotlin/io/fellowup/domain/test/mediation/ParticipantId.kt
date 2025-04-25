package io.fellowup.domain.test.mediation

import io.fellowup.domain.mediation.ParticipantId
import java.util.*

fun ParticipantId(): ParticipantId = ParticipantId(id = UUID.randomUUID())
