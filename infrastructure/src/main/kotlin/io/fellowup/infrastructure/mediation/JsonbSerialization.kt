package io.fellowup.infrastructure.mediation

import io.fellowup.domain.mediation.ParticipantId
import java.util.*

val JSONB_PARTICIPANTS_SERIALIZER: (Set<ParticipantId>) -> String =
    { participants -> participants.map { it.id }.joinToString(",") }

val JSONB_PARTICIPANTS_DESERIALIZER: (String) -> Set<ParticipantId> =
    { participants -> participants.split(",").map { ParticipantId(UUID.fromString(it)) }.toSet() }
