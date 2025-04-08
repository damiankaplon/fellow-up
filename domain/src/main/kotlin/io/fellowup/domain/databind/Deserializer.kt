package io.fellowup.domain.databind

fun interface Deserializer<T> {

    operator fun invoke(value: String): T
}
