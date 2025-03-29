package io.fellowup.kafka.infra.ktor

import io.ktor.server.config.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.*

fun ApplicationConfig.kafkaConsumerProperties(): Properties {
    val bootstrapServers = this.property("kafka.bootstrapServers").getString()
    val groupId = this.property("kafka.groupId").getString()
    val clientName = this.property("kafka.clientName").getString()
    val keyDeserializer =
        this.propertyOrNull("kafka.keyDeserializer")?.getString() ?: StringDeserializer::class.qualifiedName
    val valueDeserializer =
        this.propertyOrNull("kafka.valueDeserializer")?.getString() ?: StringDeserializer::class.qualifiedName
    val autoOffsetReset = this.propertyOrNull("kafka.autoOffsetReset")?.getString() ?: "latest"
    val enableAutoCommit = this.propertyOrNull("kafka.enableAutoCommit")?.getString()?.toBoolean() ?: false

    val properties = mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
        ConsumerConfig.GROUP_ID_CONFIG to groupId,
        ConsumerConfig.CLIENT_ID_CONFIG to clientName,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to keyDeserializer,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to valueDeserializer,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to autoOffsetReset,
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to enableAutoCommit,
    )
    return Properties().apply { putAll(properties) }
}
