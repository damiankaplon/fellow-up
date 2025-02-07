package io.fellowup.test

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun String.utcInstant(): Instant = LocalDateTime.parse(this).toInstant(ZoneOffset.UTC)