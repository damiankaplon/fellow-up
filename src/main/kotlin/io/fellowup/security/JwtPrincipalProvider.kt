package io.fellowup.security

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

fun RoutingCall.jwtPrincipalOrThrow(): JWTPrincipal = this.principal<JWTPrincipal>()
    ?: throw NoJwtException("No JWT principal found")

fun JWTPrincipal.subjectOrThrow(): String = this.subject
    ?: throw NoAuthenticatedSubjectException("No authenticated subject found in $this")