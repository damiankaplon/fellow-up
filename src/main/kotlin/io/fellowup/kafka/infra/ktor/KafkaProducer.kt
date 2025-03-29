package io.fellowup.kafka.infra.ktor

import io.fellowup.kafka.serialization.infra.JacksonKObjectSerializer
import io.ktor.server.config.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig

fun ApplicationConfig.KafkaProducer(): KafkaProducer<String, Any> {
    val bootstrapServers = this.property("kafka.bootstrapServers").getString()
    val clientName = this.property("kafka.clientName").getString()
    val keySerializer = this.propertyOrNull("kafka.keySerializer")?.getString()
        ?: "org.apache.kafka.common.serialization.StringSerializer"
    val valueSerializer =
        this.propertyOrNull("kafka.valueSerializer")?.getString() ?: JacksonKObjectSerializer::class.qualifiedName
    val properties = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to keySerializer,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to valueSerializer,
        ProducerConfig.CLIENT_ID_CONFIG to clientName
    )
    return KafkaProducer<String, Any>(properties)
}