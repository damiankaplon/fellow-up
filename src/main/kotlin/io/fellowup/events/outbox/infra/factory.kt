package io.fellowup.events.outbox.infra

import io.fellowup.db.TransactionalRunner
import io.fellowup.events.EventPublisher

fun <T> createOutboxPublisher(
    transactionalRunner: TransactionalRunner,
    defaultTopic: EventPublisher.Topic
): EventPublisher<T> = OutboxPublisher(
    transactionalRunner = transactionalRunner,
    defaultTopic = defaultTopic
)