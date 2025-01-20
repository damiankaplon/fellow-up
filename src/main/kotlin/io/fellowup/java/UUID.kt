package io.fellowup.java

import java.util.UUID

fun String?.toUUID(): UUID = UUID.fromString(this)