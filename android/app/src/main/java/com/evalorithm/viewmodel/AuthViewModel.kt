package com.evalorithm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evalorithm.data.model.AuthResponse
import com.evalorithm.data.model.LoginRequest
import com.evalorithm.data.model.RegisterRequest
import com.evalorithm.data.repository.AuthRepository
import com.evalorithm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<Resource<AuthResponse>>()
    val loginState: LiveData<Resource<AuthResponse>> = _loginState

    private val _registerState = MutableLiveData<Resource<AuthResponse>>()
    val registerState: LiveData<Resource<AuthResponse>> = _registerState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            _loginState.value = repository.login(LoginRequest(email, password))
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
        role: String
    ) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            _registerState.value = repository.register(
                RegisterRequest(firstName, lastName, email, password, phone, role)
            )
        }
    }
}
