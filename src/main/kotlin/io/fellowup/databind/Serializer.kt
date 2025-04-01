package io.fellowup.databind

fun interface Serializer<T> {

    operator fun invoke(value: T): String
}
