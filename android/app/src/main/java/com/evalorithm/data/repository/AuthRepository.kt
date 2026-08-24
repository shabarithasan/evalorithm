package com.evalorithm.data.repository

import com.evalorithm.data.model.AuthResponse
import com.evalorithm.data.model.LoginRequest
import com.evalorithm.data.model.RegisterRequest
import com.evalorithm.util.Resource

interface AuthRepository {
    suspend fun login(request: LoginRequest): Resource<AuthResponse>
    suspend fun register(request: RegisterRequest): Resource<AuthResponse>
    suspend fun logout()
}
