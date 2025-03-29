package io.fellowup.test.events

import io.fellowup.events.EventPublisher
import io.fellowup.events.Topic

internal class NopEventPublisher<T> : EventPublisher<T> {
    override suspend fun publish(event: T, topic: Topic?) {
    }
}