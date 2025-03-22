package io.fellowup.kafka.serialization

import org.apache.kafka.common.serialization.Serializer

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
