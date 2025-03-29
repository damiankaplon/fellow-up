package io.fellowup.kafka.infra

import org.apache.kafka.clients.consumer.ConsumerRecord

interface KafkaRecordConsumer {
    val topic: String
    fun consume(record: ConsumerRecord<String, String>)
}
