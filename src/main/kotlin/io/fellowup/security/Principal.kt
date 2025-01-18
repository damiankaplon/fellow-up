package io.fellowup.security

import java.util.UUID

interface Principal {
    val userId: UUID
}

