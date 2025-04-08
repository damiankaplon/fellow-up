package io.fellowup.domain.databind

fun interface Serializer<T> {

    operator fun invoke(value: T): String
}
