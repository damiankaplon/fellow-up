package io.fellowup.infrastructure.kotlinx.serialization

import kotlinx.serialization.Serializable
import java.util.*

@Serializable(with = UuidSerializer::class)
data class Uuid(val value: String) {
    fun toJava(): UUID = UUID.fromString(value)
    override fun toString(): String = this.value
}

fun UUID.toKotlinx(): Uuid = Uuid(toString())