package io.fellowup.databind

fun interface Deserializer<T> {

    operator fun invoke(value: String): T
}
