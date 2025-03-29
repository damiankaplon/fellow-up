package io.fellowup.test.events

import io.fellowup.events.EventPublisher

internal class NopEventPublisher<T> : EventPublisher<T> {
    override suspend fun publish(event: T, topic: EventPublisher.Topic?) {
    }
}