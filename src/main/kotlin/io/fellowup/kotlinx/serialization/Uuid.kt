package io.fellowup.kotlinx.serialization

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable(with = UuidSerializer::class)
data class Uuid(val value: String) {
    fun toJava(): UUID = UUID.fromString(value)
}

fun UUID.toKotlinx(): Uuid = Uuid(toString())