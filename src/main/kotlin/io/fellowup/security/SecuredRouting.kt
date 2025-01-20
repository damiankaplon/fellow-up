package io.fellowup.security

import io.ktor.server.routing.*

fun interface SecuredRouting : (Routing, Route.() -> Unit) -> Unit
