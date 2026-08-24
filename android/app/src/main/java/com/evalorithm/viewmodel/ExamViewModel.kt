package com.evalorithm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evalorithm.data.api.ApiInterface
import com.evalorithm.data.local.TokenManager
import com.evalorithm.data.model.Exam
import com.evalorithm.data.model.ExamDashboardData
import com.evalorithm.data.model.ExamResult
import com.evalorithm.data.model.LiveExamQuestion
import com.evalorithm.data.model.PageResponse
import com.evalorithm.data.model.SubmitExamResult
import com.evalorithm.data.repository.MainRepository
import com.evalorithm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val repository: MainRepository,
    private val api: ApiInterface,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _exams = MutableLiveData<Resource<PageResponse<Exam>>>()
    val exams: LiveData<Resource<PageResponse<Exam>>> = _exams

    private val _examDetail = MutableLiveData<Resource<Exam>>()
    val examDetail: LiveData<Resource<Exam>> = _examDetail

    private val _dashboard = MutableLiveData<Resource<ExamDashboardData>>()
    val dashboard: LiveData<Resource<ExamDashboardData>> = _dashboard

    private val _liveExam = MutableLiveData<Resource<LiveExamQuestion>>()
    val liveExam: LiveData<Resource<LiveExamQuestion>> = _liveExam

    private val _submitResult = MutableLiveData<Resource<SubmitExamResult>>()
    val submitResult: LiveData<Resource<SubmitExamResult>> = _submitResult

    private val _results = MutableLiveData<Resource<PageResponse<ExamResult>>>()
    val results: LiveData<Resource<PageResponse<ExamResult>>> = _results

    private val _singleResult = MutableLiveData<Resource<ExamResult>>()
    val singleResult: LiveData<Resource<ExamResult>> = _singleResult

    var currentAttemptId: Long? = null
    var currentQuestionIndex: Int = 0

    fun loadExams(page: Int = 0, size: Int = 10, status: String? = null) {
        viewModelScope.launch {
            _exams.value = Resource.Loading()
            _exams.value = repository.getExams(page, size, status)
        }
    }

    fun loadExamDetail(id: Long) {
        viewModelScope.launch {
            _examDetail.value = Resource.Loading()
            _examDetail.value = repository.getExam(id)
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _dashboard.value = Resource.Loading()
            _dashboard.value = repository.getExamDashboard()
        }
    }

    fun startExam(examId: Long) {
        viewModelScope.launch {
            _liveExam.value = Resource.Loading()
            try {
                val response = api.startExam(mapOf("examId" to examId))
                if (response.success && response.data != null) {
                    val attemptId = (response.data as Map<*, *>)["attemptId"] as? Long
                    currentAttemptId = attemptId
                    currentQuestionIndex = 0
                    loadQuestion(0)
                } else {
                    _liveExam.value = Resource.Error(response.message)
                }
            } catch (e: Exception) {
                _liveExam.value = Resource.Error(e.message ?: "Failed to start exam")
            }
        }
    }

    fun loadQuestion(index: Int) {
        viewModelScope.launch {
            _liveExam.value = Resource.Loading()
            try {
                val response = api.getExamQuestion(currentAttemptId!!, index)
                if (response.success && response.data != null) {
                    currentQuestionIndex = index
                    _liveExam.value = Resource.Success(response.data!!)
                } else {
                    _liveExam.value = Resource.Error(response.message)
                }
            } catch (e: Exception) {
                _liveExam.value = Resource.Error(e.message ?: "Failed to load question")
            }
        }
    }

    fun saveAnswer(examQuestionId: Long, selectedOption: String?, textAnswer: String?) {
        viewModelScope.launch {
            try {
                val answer = mutableMapOf<String, Any>(
                    "examQuestionId" to examQuestionId
                )
                selectedOption?.let { answer["selectedOptionLabel"] = it }
                textAnswer?.let { answer["textAnswer"] = it }
                api.saveExamAnswer(currentAttemptId!!, answer)
            } catch (_: Exception) {}
        }
    }

    fun submitExam() {
        viewModelScope.launch {
            _submitResult.value = Resource.Loading()
            try {
                val response = api.submitExam(currentAttemptId!!)
                if (response.success && response.data != null) {
                    _submitResult.value = Resource.Success(response.data!!)
                } else {
                    _submitResult.value = Resource.Error(response.message)
                }
            } catch (e: Exception) {
                _submitResult.value = Resource.Error(e.message ?: "Failed to submit")
            }
        }
    }

    fun resumeExam(attemptId: Long) {
        viewModelScope.launch {
            _liveExam.value = Resource.Loading()
            currentAttemptId = attemptId
            try {
                val response = api.resumeExam(attemptId)
                if (response.success && response.data != null) {
                    _liveExam.value = Resource.Success(response.data!!)
                } else {
                    _liveExam.value = Resource.Error(response.message)
                }
            } catch (e: Exception) {
                _liveExam.value = Resource.Error(e.message ?: "Failed to resume")
            }
        }
    }

    fun loadStudentResults(studentId: Long, page: Int = 0) {
        viewModelScope.launch {
            _results.value = Resource.Loading()
            _results.value = repository.getStudentResults(studentId, page)
        }
    }
}
