package io.fellowup.databind.infra

import io.fellowup.databind.Deserializer
import io.fellowup.databind.Serializer


inline fun <reified T> Serializer(): Serializer<T> =
    Serializer { JACKSON_OBJECT_MAPPER.writeValueAsString(it) }

inline fun <reified T> Deserializer(): Deserializer<T> =
    Deserializer { JACKSON_OBJECT_MAPPER.readValue(it, T::class.java) }