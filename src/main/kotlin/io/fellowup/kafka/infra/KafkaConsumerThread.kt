package io.fellowup.kafka.infra

import io.fellowup.db.TransactionalRunner
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

fun KafkaConsumerThread(
    kafkaConsumerProperties: Properties,
    kafkaRecordConsumer: KafkaRecordConsumer,
    transactionalRunner: TransactionalRunner,
    threadName: String = "kafka-consumer-${kafkaRecordConsumer.topic}-${UUID.randomUUID()}",
    logger: org.slf4j.Logger = LoggerFactory.getLogger(threadName)
): KafkaConsumerThread =
    KafkaConsumerThread(threadName, kafkaConsumerProperties, kafkaRecordConsumer.topic) { kafkaConsumer ->
        runBlocking {
            try {
                transactionalRunner.transaction {
                    val consumerRecords = kafkaConsumer.poll(300.milliseconds.toJavaDuration())
                    consumerRecords.records(kafkaRecordConsumer.topic).forEach { record ->
                        kafkaRecordConsumer.consume(record)
                    }
                    kafkaConsumer.commitSync()
                }
            } catch (e: Throwable) {
                logger.error("Error consuming kafka events for: ${kafkaRecordConsumer.topic}", e)
            }
        }
    }

class KafkaConsumerThread(
    name: String,
    kafkaConsumerProperties: Properties,
    private val topic: String,
    private val block: (KafkaConsumer<String, String>) -> Unit
) : Thread(name) {

    private val kafkaConsumer = KafkaConsumer<String, String>(kafkaConsumerProperties)
    private val isToClose = AtomicBoolean(false)
    private val isClosed = AtomicBoolean(false)

    override fun run() {
        kafkaConsumer.subscribe(listOf(topic))
        while (isToClose.get().not()) {
            block.invoke(kafkaConsumer)
        }
        if (isToClose.get()) kafkaConsumer.close()
        isClosed.set(true)
        this.interrupt()
    }

    fun close() {
        if (isClosed.get()) this.interrupt()
        isToClose.set(true)
    }
}
