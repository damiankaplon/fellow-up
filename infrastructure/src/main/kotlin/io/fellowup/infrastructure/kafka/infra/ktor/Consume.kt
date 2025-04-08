package io.fellowup.infrastructure.kafka.infra.ktor

import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.events.EventConsumer
import io.fellowup.domain.events.Topic
import io.fellowup.infrastructure.databind.JACKSON_OBJECT_MAPPER
import io.fellowup.infrastructure.kafka.infra.KafkaConsumerThread
import io.fellowup.infrastructure.kafka.infra.KafkaRecordConsumer
import io.ktor.server.application.*
import org.apache.kafka.clients.consumer.ConsumerRecord

inline fun <reified T> Application.consume(
    transactionalRunner: TransactionalRunner,
    eventConsumer: EventConsumer<T>,
    topic: Topic,
) {
    val kafkaRecordConsumer = object : KafkaRecordConsumer {
        override val topic: String = topic.name
        override suspend fun consume(record: ConsumerRecord<String, String>) {
            val event = JACKSON_OBJECT_MAPPER.readValue(record.value(), T::class.java)
            eventConsumer.handle(event)
        }
    }
    val kafkaConsumerThread = KafkaConsumerThread(
        kafkaConsumerProperties = environment.config.kafkaConsumerProperties(),
        kafkaRecordConsumer = kafkaRecordConsumer,
        transactionalRunner = transactionalRunner,
    )
    monitor.subscribe(ApplicationStarted) {
        kafkaConsumerThread.start()
    }
    monitor.subscribe(ApplicationStopping) {
        kafkaConsumerThread.close()
        kafkaConsumerThread.join()
    }

}
