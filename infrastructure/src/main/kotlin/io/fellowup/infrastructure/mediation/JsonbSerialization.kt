package io.fellowup.infrastructure.mediation

import io.fellowup.domain.mediation.ParticipantId
import java.util.*

val JSONB_PARTICIPANTS_SERIALIZER: (Set<ParticipantId>) -> String =
    { participants -> "[" + participants.joinToString(",") { "\"${it.id}\"" } + "]" }

val JSONB_PARTICIPANTS_DESERIALIZER: (String) -> Set<ParticipantId> =
    { participants ->
        participants.removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .map { it.removePrefix("\"").removeSuffix("\"").let { uuid -> ParticipantId(UUID.fromString(uuid)) } }
            .toSet()
    }
