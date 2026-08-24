package com.evalorithm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evalorithm.data.api.ApiInterface
import com.evalorithm.data.local.TokenManager
import com.evalorithm.data.model.Attainment
import com.evalorithm.data.model.Backup
import com.evalorithm.data.model.Certificate
import com.evalorithm.data.model.CourseOutcome
import com.evalorithm.data.model.Feedback
import com.evalorithm.data.model.SystemSetting
import com.evalorithm.data.model.SupportTicket
import com.evalorithm.data.repository.MainRepository
import com.evalorithm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OBEViewModel @Inject constructor(
    private val repository: MainRepository,
    private val api: ApiInterface,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _cos = MutableLiveData<Resource<List<CourseOutcome>>>()
    val cos: LiveData<Resource<List<CourseOutcome>>> = _cos

    private val _attainment = MutableLiveData<Resource<List<Attainment>>>()
    val attainment: LiveData<Resource<List<Attainment>>> = _attainment

    private val _certificates = MutableLiveData<Resource<List<Certificate>>>()
    val certificates: LiveData<Resource<List<Certificate>>> = _certificates

    private val _feedback = MutableLiveData<Resource<List<Feedback>>>()
    val feedback: LiveData<Resource<List<Feedback>>> = _feedback

    private val _tickets = MutableLiveData<Resource<List<SupportTicket>>>()
    val tickets: LiveData<Resource<List<SupportTicket>>> = _tickets

    private val _settings = MutableLiveData<Resource<List<SystemSetting>>>()
    val settings: LiveData<Resource<List<SystemSetting>>> = _settings

    private val _backups = MutableLiveData<Resource<List<Backup>>>()
    val backups: LiveData<Resource<List<Backup>>> = _backups

    fun loadCOs(subjectId: Long) {
        viewModelScope.launch {
            _cos.value = Resource.Loading()
            _cos.value = repository.getCOsBySubject(subjectId)
        }
    }

    fun loadAttainment(subjectId: Long, semesterId: Long) {
        viewModelScope.launch {
            _attainment.value = Resource.Loading()
            _attainment.value = repository.getSubjectAttainment(subjectId, semesterId)
        }
    }

    fun loadCertificates(studentId: Long) {
        viewModelScope.launch {
            _certificates.value = Resource.Loading()
            _certificates.value = repository.getStudentCertificates(studentId)
        }
    }

    fun loadFeedback(userId: Long) {
        viewModelScope.launch {
            _feedback.value = Resource.Loading()
            _feedback.value = repository.getReceivedFeedback(userId)
        }
    }

    fun loadTickets(userId: Long) {
        viewModelScope.launch {
            _tickets.value = Resource.Loading()
            _tickets.value = repository.getMyTickets(userId)
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            _settings.value = Resource.Loading()
            _settings.value = repository.getSystemSettings()
        }
    }

    fun loadBackups() {
        viewModelScope.launch {
            _backups.value = Resource.Loading()
            _backups.value = repository.getBackups()
        }
    }

    fun submitFeedback(type: String, toUserId: Long?, subjectId: Long?, rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                val data = mutableMapOf<String, Any>(
                    "feedbackType" to type,
                    "rating" to rating,
                    "comment" to comment
                )
                toUserId?.let { data["toUserId"] = it }
                subjectId?.let { data["subjectId"] = it }
                api.submitFeedback(data)
            } catch (_: Exception) {
            }
        }
    }

    fun createTicket(subject: String, description: String) {
        viewModelScope.launch {
            try {
                api.createTicket(mapOf("subject" to subject, "description" to description))
            } catch (_: Exception) {
            }
        }
    }

    fun createBackup() {
        viewModelScope.launch {
            _backups.value = Resource.Loading()
            try {
                api.createBackup()
                loadBackups()
            } catch (_: Exception) {
            }
        }
    }
}
