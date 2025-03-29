package io.fellowup.events

fun interface EventConsumer<T> {

    fun handle(event: T)
}
