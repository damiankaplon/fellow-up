package io.fellowup.kafka.infra

import io.fellowup.db.TransactionalRunner
import io.fellowup.events.outbox.infra.OutboxDao
import io.fellowup.events.outbox.infra.OutboxPublishedDao
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import kotlin.coroutines.coroutineContext

class KafkaOutboxService(
    private val transactionalRunner: TransactionalRunner,
    private val kafkaProducer: KafkaProducer<String, Any>
) {

    suspend fun publishOutbox() {
        val published = transactionalRunner.transaction(java.sql.Connection.TRANSACTION_SERIALIZABLE) {
            val entries: Iterable<OutboxDao> = OutboxDao.all()
            val published = entries.map { entry ->
                val published = OutboxPublishedDao.new {
                    sentTo = entry.destination
                    message = entry.message
                }
                entry.delete()
                published
            }
            return@transaction published
        }
        withContext(coroutineContext) {
            published.forEach { entry ->
                val producerRecord = ProducerRecord<String, Any>(entry.sentTo, entry.message)
                kafkaProducer.send(producerRecord).get()
            }
        }
    }
}
