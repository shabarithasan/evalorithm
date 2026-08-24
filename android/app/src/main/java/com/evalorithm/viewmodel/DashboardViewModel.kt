package com.evalorithm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.evalorithm.data.local.TokenManager
import com.evalorithm.data.model.DashboardData
import com.evalorithm.data.model.FacultyDashboardData
import com.evalorithm.data.model.Notification
import com.evalorithm.data.model.StudentDashboardData
import com.evalorithm.data.model.User
import com.evalorithm.data.repository.MainRepository
import com.evalorithm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: MainRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _adminDashboard = MutableLiveData<Resource<DashboardData>>()
    val adminDashboard: LiveData<Resource<DashboardData>> = _adminDashboard

    private val _facultyDashboard = MutableLiveData<Resource<FacultyDashboardData>>()
    val facultyDashboard: LiveData<Resource<FacultyDashboardData>> = _facultyDashboard

    private val _studentDashboard = MutableLiveData<Resource<StudentDashboardData>>()
    val studentDashboard: LiveData<Resource<StudentDashboardData>> = _studentDashboard

    private val _notifications = MutableLiveData<Resource<List<Notification>>>()
    val notifications: LiveData<Resource<List<Notification>>> = _notifications

    private val _unreadCount = MutableLiveData<Resource<Int>>()
    val unreadCount: LiveData<Resource<Int>> = _unreadCount

    private val _profile = MutableLiveData<Resource<User>>()
    val profile: LiveData<Resource<User>> = _profile

    val userRole = tokenManager.getUserRole().asLiveData()
    val userName = tokenManager.getUserName().asLiveData()

    fun loadAdminDashboard() {
        viewModelScope.launch {
            _adminDashboard.value = Resource.Loading()
            _adminDashboard.value = repository.getAdminDashboard()
        }
    }

    fun loadFacultyDashboard() {
        viewModelScope.launch {
            _facultyDashboard.value = Resource.Loading()
            _facultyDashboard.value = repository.getFacultyDashboard()
        }
    }

    fun loadStudentDashboard() {
        viewModelScope.launch {
            _studentDashboard.value = Resource.Loading()
            _studentDashboard.value = repository.getStudentDashboard()
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _notifications.value = Resource.Loading()
            _notifications.value = repository.getNotifications()
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            _unreadCount.value = repository.getUnreadCount()
        }
    }

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
            loadNotifications()
            loadUnreadCount()
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profile.value = Resource.Loading()
            _profile.value = repository.getProfile()
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearAll()
        }
    }
}
