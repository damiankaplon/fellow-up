package io.fellowup.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer

fun interface JsonMapper {
    fun toString(obj: Any): String
}

fun interface KafkaStringSerializer {
    fun serialize(topic: String?, data: String?): ByteArray?
}

open class KObjectSerializer(
    private val jsonMapper: JsonMapper,
    private val kafkaStringSerializer: KafkaStringSerializer
) : Serializer<Any> {

    override fun serialize(topic: String?, data: Any?): ByteArray? {
        if (data == null) return null
        val jsonString = jsonMapper.toString(data)
       return kafkaStringSerializer.serialize(topic, jsonString)
    }
}

private val objectMapper = jacksonObjectMapper()
private val stringSerializer = StringSerializer()

object KObjectMapperObjectSerializer : KObjectSerializer(
    JsonMapper { objectMapper.writeValueAsString(it) },
    KafkaStringSerializer { topic, data -> stringSerializer.serialize(topic, data) }
)
