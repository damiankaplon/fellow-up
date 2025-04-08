package io.fellowup.infrastructure.security

import java.util.UUID

interface Principal {
    val userId: UUID
}

