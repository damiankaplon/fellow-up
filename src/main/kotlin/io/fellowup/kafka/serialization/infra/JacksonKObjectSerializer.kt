package io.fellowup.kafka.serialization.infra

import io.fellowup.databind.infra.JACKSON_OBJECT_MAPPER
import io.fellowup.kafka.serialization.JsonMapper
import io.fellowup.kafka.serialization.KObjectSerializer
import io.fellowup.kafka.serialization.KafkaStringSerializer
import org.apache.kafka.common.serialization.StringSerializer

private val STRING_SERIALIZER = StringSerializer()

class JacksonKObjectSerializer : KObjectSerializer(
    JsonMapper { JACKSON_OBJECT_MAPPER.writeValueAsString(it) },
    KafkaStringSerializer { topic, data -> STRING_SERIALIZER.serialize(topic, data) }
)
