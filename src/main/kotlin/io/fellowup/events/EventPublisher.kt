package io.fellowup.events

/**
 * When no topic specified, some default should be assumed
 */
interface EventPublisher<T> {

    fun publish(event: T, topic: String? = null)

    fun publish(events: Set<T>, topic: String? = null) = events.forEach { publish(it, topic) }
}
