package io.fellowup.domain.test.fixtures.events

import io.fellowup.domain.events.EventPublisher
import io.fellowup.domain.events.Topic

class EventStoringPublisher<T> : EventPublisher<T> {

    private var _events = listOf<T>()
    val events: List<T> get() = _events

    override suspend fun publish(event: T, topic: Topic?) {
        _events = _events + event
    }
}