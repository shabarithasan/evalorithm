package com.evalorithm.data.model

data class User(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val profilePhotoUrl: String?,
    val role: String,
    val enabled: Boolean,
    val emailVerified: Boolean,
    val createdAt: String
)
