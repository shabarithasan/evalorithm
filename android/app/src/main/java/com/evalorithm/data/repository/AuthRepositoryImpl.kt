package com.evalorithm.data.repository

import com.evalorithm.data.api.ApiInterface
import com.evalorithm.data.local.TokenManager
import com.evalorithm.data.model.AuthResponse
import com.evalorithm.data.model.LoginRequest
import com.evalorithm.data.model.RegisterRequest
import com.evalorithm.util.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiInterface,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Resource<AuthResponse> {
        return try {
            val response = api.login(request)
            if (response.success && response.data != null) {
                tokenManager.saveAuthData(response.data)
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun register(request: RegisterRequest): Resource<AuthResponse> {
        return try {
            val response = api.register(request)
            if (response.success && response.data != null) {
                tokenManager.saveAuthData(response.data)
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun logout() {
        tokenManager.clearAll()
    }
}
