package io.fellowup.domain.events

fun interface EventConsumer<T> {

    suspend fun handle(event: T)
}
