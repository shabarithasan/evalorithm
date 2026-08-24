package com.evalorithm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evalorithm.data.model.PageResponse
import com.evalorithm.data.model.Question
import com.evalorithm.data.model.QuestionCategory
import com.evalorithm.data.model.QuestionDashboardData
import com.evalorithm.data.model.QuestionVersion
import com.evalorithm.data.repository.MainRepository
import com.evalorithm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _categories = MutableLiveData<Resource<PageResponse<QuestionCategory>>>()
    val categories: LiveData<Resource<PageResponse<QuestionCategory>>> = _categories

    private val _questions = MutableLiveData<Resource<PageResponse<Question>>>()
    val questions: LiveData<Resource<PageResponse<Question>>> = _questions

    private val _questionDetail = MutableLiveData<Resource<Question>>()
    val questionDetail: LiveData<Resource<Question>> = _questionDetail

    private val _dashboard = MutableLiveData<Resource<QuestionDashboardData>>()
    val dashboard: LiveData<Resource<QuestionDashboardData>> = _dashboard

    private val _versions = MutableLiveData<Resource<List<QuestionVersion>>>()
    val versions: LiveData<Resource<List<QuestionVersion>>> = _versions

    private val _createState = MutableLiveData<Resource<Question>>()
    val createState: LiveData<Resource<Question>> = _createState

    private val _deleteState = MutableLiveData<Resource<Unit>>()
    val deleteState: LiveData<Resource<Unit>> = _deleteState

    private val _categoryCreateState = MutableLiveData<Resource<QuestionCategory>>()
    val categoryCreateState: LiveData<Resource<QuestionCategory>> = _categoryCreateState

    var selectedDepartmentId: Long? = null
    var selectedSemesterId: Long? = null
    var selectedSubjectId: Long? = null
    var selectedType: String? = null
    var selectedDifficulty: String? = null
    var selectedStatus: String? = null
    var searchQuery: String? = null

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = Resource.Loading()
            _categories.value = repository.getQuestionCategories(0, 100)
        }
    }

    fun loadQuestions(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _questions.value = Resource.Loading()
            _questions.value = repository.getQuestions(
                page, size, selectedDepartmentId, selectedSemesterId,
                selectedSubjectId, selectedType, selectedDifficulty,
                selectedStatus, searchQuery
            )
        }
    }

    fun loadQuestion(id: Long) {
        viewModelScope.launch {
            _questionDetail.value = Resource.Loading()
            _questionDetail.value = repository.getQuestion(id)
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _dashboard.value = Resource.Loading()
            _dashboard.value = repository.getQuestionDashboard()
        }
    }

    fun loadVersions(questionId: Long) {
        viewModelScope.launch {
            _versions.value = Resource.Loading()
            _versions.value = repository.getQuestionVersions(questionId)
        }
    }

    fun createQuestion(request: Map<String, Any>) {
        viewModelScope.launch {
            _createState.value = Resource.Loading()
            _createState.value = repository.createQuestion(request)
        }
    }

    fun deleteQuestion(id: Long) {
        viewModelScope.launch {
            _deleteState.value = Resource.Loading()
            _deleteState.value = repository.deleteQuestion(id)
            loadQuestions()
        }
    }

    fun archiveQuestion(id: Long) {
        viewModelScope.launch {
            repository.archiveQuestion(id)
            loadQuestions()
        }
    }

    fun restoreQuestion(id: Long) {
        viewModelScope.launch {
            repository.restoreQuestion(id)
            loadQuestions()
        }
    }

    fun submitForReview(id: Long) {
        viewModelScope.launch {
            repository.submitForReview(id)
            loadQuestion(id)
        }
    }

    fun approveQuestion(id: Long, status: String, comments: String) {
        viewModelScope.launch {
            repository.approveQuestion(id, status, comments)
            loadQuestion(id)
        }
    }

    fun duplicateQuestion(id: Long) {
        viewModelScope.launch {
            repository.duplicateQuestion(id)
            loadQuestions()
        }
    }

    fun createCategory(request: Map<String, String>) {
        viewModelScope.launch {
            _categoryCreateState.value = Resource.Loading()
            _categoryCreateState.value = repository.createQuestionCategory(request)
            loadCategories()
        }
    }

    fun setFilters(
        departmentId: Long?, semesterId: Long?, subjectId: Long?,
        type: String?, difficulty: String?, status: String?, search: String?
    ) {
        selectedDepartmentId = departmentId
        selectedSemesterId = semesterId
        selectedSubjectId = subjectId
        selectedType = type
        selectedDifficulty = difficulty
        selectedStatus = status
        searchQuery = search
        loadQuestions()
    }

    fun clearFilters() {
        selectedDepartmentId = null
        selectedSemesterId = null
        selectedSubjectId = null
        selectedType = null
        selectedDifficulty = null
        selectedStatus = null
        searchQuery = null
        loadQuestions()
    }
}
