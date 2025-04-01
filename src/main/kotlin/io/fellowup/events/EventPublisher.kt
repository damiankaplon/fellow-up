package io.fellowup.events

/**
 * When no topic specified, some default should be assumed
 */
interface EventPublisher<T> {

    suspend fun publish(event: T, topic: Topic? = null)

    suspend fun publish(events: Set<T>, topic: Topic? = null) = events.forEach { publish(it, topic) }
}
