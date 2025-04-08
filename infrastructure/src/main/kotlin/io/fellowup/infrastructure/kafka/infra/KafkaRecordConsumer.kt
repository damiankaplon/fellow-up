package io.fellowup.infrastructure.kafka.infra

import org.apache.kafka.clients.consumer.ConsumerRecord

interface KafkaRecordConsumer {
    val topic: String
    suspend fun consume(record: ConsumerRecord<String, String>)
}
