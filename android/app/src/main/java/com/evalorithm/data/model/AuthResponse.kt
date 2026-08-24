package com.evalorithm.data.model

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val userId: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String
)
