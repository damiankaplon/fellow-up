package io.fellowup.domain.test.fixtures.events

import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.events.Topic

class NopEventPublisher<T> : EventPublisher<T> {
    override suspend fun publish(event: T, topic: Topic?) {
    }
}
