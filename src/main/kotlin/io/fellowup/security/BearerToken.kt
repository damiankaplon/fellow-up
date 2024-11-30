package io.fellowup.security

import kotlinx.serialization.Serializable

@Serializable
data class OAuthAuth(
    val token: String,
    val refreshToken: String?,
    val expiresIn: Int,
)