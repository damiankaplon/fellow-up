package io.fellowup.infrastructure.events.outbox.infra

import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.events.Topic


class OutboxPublisher<T>(
    private val transactionalRunner: TransactionalRunner,
    private val defaultTopic: Topic
) : EventPublisher<T> {

    override suspend fun publish(event: T, topic: Topic?): Unit =
        transactionalRunner.transaction(isolation = java.sql.Connection.TRANSACTION_SERIALIZABLE, readOnly = false) {
            OutboxDao.new {
                this.destination = topic?.name ?: defaultTopic.name
                this.message = event as Any
            }
        }
}
