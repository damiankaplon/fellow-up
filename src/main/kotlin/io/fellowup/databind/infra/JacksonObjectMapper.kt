package io.fellowup.databind.infra

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val JACKSON_OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
