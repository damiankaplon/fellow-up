package io.fellowup.events

fun interface EventConsumer<T> {

    suspend fun handle(event: T)
}
