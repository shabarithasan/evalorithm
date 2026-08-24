package com.evalorithm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evalorithm.data.api.ApiInterface
import com.evalorithm.data.local.TokenManager
import com.evalorithm.data.model.AdaptiveQuestion
import com.evalorithm.data.model.AdaptiveSession
import com.evalorithm.data.model.AIDashboardData
import com.evalorithm.data.model.AIInsight
import com.evalorithm.data.model.AIQuestion
import com.evalorithm.data.model.LeaderboardItem
import com.evalorithm.data.model.PageResponse
import com.evalorithm.data.model.Prediction
import com.evalorithm.data.model.Recommendation
import com.evalorithm.data.model.StudentAnalytics
import com.evalorithm.data.repository.MainRepository
import com.evalorithm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val repository: MainRepository,
    private val api: ApiInterface,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _aiDashboard = MutableLiveData<Resource<AIDashboardData>>()
    val aiDashboard: LiveData<Resource<AIDashboardData>> = _aiDashboard

    private val _aiQuestions = MutableLiveData<Resource<PageResponse<AIQuestion>>>()
    val aiQuestions: LiveData<Resource<PageResponse<AIQuestion>>> = _aiQuestions

    private val _generatedQuestions = MutableLiveData<Resource<List<AIQuestion>>>()
    val generatedQuestions: LiveData<Resource<List<AIQuestion>>> = _generatedQuestions

    private val _adaptiveSession = MutableLiveData<Resource<AdaptiveSession>>()
    val adaptiveSession: LiveData<Resource<AdaptiveSession>> = _adaptiveSession

    private val _adaptiveQuestion = MutableLiveData<Resource<AdaptiveQuestion>>()
    val adaptiveQuestion: LiveData<Resource<AdaptiveQuestion>> = _adaptiveQuestion

    private val _studentAnalytics = MutableLiveData<Resource<StudentAnalytics>>()
    val studentAnalytics: LiveData<Resource<StudentAnalytics>> = _studentAnalytics

    private val _recommendations = MutableLiveData<Resource<List<Recommendation>>>()
    val recommendations: LiveData<Resource<List<Recommendation>>> = _recommendations

    private val _predictions = MutableLiveData<Resource<List<Prediction>>>()
    val predictions: LiveData<Resource<List<Prediction>>> = _predictions

    private val _leaderboard = MutableLiveData<Resource<List<LeaderboardItem>>>()
    val leaderboard: LiveData<Resource<List<LeaderboardItem>>> = _leaderboard

    private val _insights = MutableLiveData<Resource<List<AIInsight>>>()
    val insights: LiveData<Resource<List<AIInsight>>> = _insights

    var currentAdaptiveSessionId: Long? = null

    fun loadAIDashboard() {
        viewModelScope.launch {
            _aiDashboard.value = Resource.Loading()
            _aiDashboard.value = repository.getAIDashboard()
        }
    }

    fun generateQuestions(subjectId: Long, departmentId: Long, questionType: String, difficulty: String, count: Int) {
        viewModelScope.launch {
            _generatedQuestions.value = Resource.Loading()
            try {
                val response = api.generateAIQuestions(mapOf(
                    "subjectId" to subjectId,
                    "departmentId" to departmentId,
                    "questionType" to questionType,
                    "difficulty" to difficulty,
                    "count" to count
                ))
                if (response.success && response.data != null) {
                    _generatedQuestions.value = Resource.Success(response.data!!)
                } else {
                    _generatedQuestions.value = Resource.Error(response.message)
                }
            } catch (e: Exception) {
                _generatedQuestions.value = Resource.Error(e.message ?: "Generation failed")
            }
        }
    }

    fun loadAIQuestions(page: Int = 0, size: Int = 20, subjectId: Long? = null) {
        viewModelScope.launch {
            _aiQuestions.value = Resource.Loading()
            try {
                val response = api.getAIQuestions(page, size, subjectId)
                if (response.success && response.data != null) {
                    _aiQuestions.value = Resource.Success(response.data!!)
                } else {
                    _aiQuestions.value = Resource.Error(response.message)
                }
            } catch (e: Exception) {
                _aiQuestions.value = Resource.Error(e.message ?: "Failed to load")
            }
        }
    }

    fun approveAIQuestion(id: Long) {
        viewModelScope.launch {
            try {
                api.approveAIQuestion(id)
            } catch (_: Exception) { }
        }
    }

    fun startAdaptiveSession(subjectId: Long) {
        viewModelScope.launch {
            _adaptiveSession.value = Resource.Loading()
            try {
                val response = api.startAdaptiveSession(mapOf("subjectId" to subjectId))
                if (response.success && response.data != null) {
                    val session = response.data!!
                    currentAdaptiveSessionId = session.id
                    _adaptiveSession.value = Resource.Success(session)
                    getNextQuestion()
                } else {
                    _adaptiveSession.value = Resource.Error(response.message)
                }
            } catch (e: Exception) {
                _adaptiveSession.value = Resource.Error(e.message ?: "Failed to start")
            }
        }
    }

    fun getNextQuestion() {
        val sessionId = currentAdaptiveSessionId ?: return
        viewModelScope.launch {
            _adaptiveQuestion.value = Resource.Loading()
            try {
                val response = api.getNextAdaptiveQuestion(sessionId)
                if (response.success && response.data != null) {
                    _adaptiveQuestion.value = Resource.Success(response.data!!)
                } else {
                    _adaptiveQuestion.value = Resource.Error(response.message)
                }
            } catch (e: Exception) {
                _adaptiveQuestion.value = Resource.Error(e.message ?: "No more questions")
            }
        }
    }

    fun submitAnswer(questionId: Long, selectedOption: String?, textAnswer: String?, timeTaken: Int) {
        val sessionId = currentAdaptiveSessionId ?: return
        viewModelScope.launch {
            _adaptiveQuestion.value = Resource.Loading()
            try {
                val answer = mutableMapOf<String, Any>(
                    "questionId" to questionId,
                    "sessionId" to sessionId,
                    "timeTakenSeconds" to timeTaken
                )
                selectedOption?.let { answer["selectedOption"] = it }
                textAnswer?.let { answer["textAnswer"] = it }
                val response = api.submitAdaptiveAnswer(sessionId, answer)
                if (response.success && response.data != null) {
                    _adaptiveQuestion.value = Resource.Success(response.data!!)
                } else {
                    _adaptiveQuestion.value = Resource.Error(response.message)
                }
            } catch (e: Exception) {
                _adaptiveQuestion.value = Resource.Error(e.message ?: "Submit failed")
            }
        }
    }

    fun endAdaptiveSession() {
        val sessionId = currentAdaptiveSessionId ?: return
        viewModelScope.launch {
            _adaptiveSession.value = Resource.Loading()
            try {
                val response = api.endAdaptiveSession(sessionId)
                if (response.success && response.data != null) {
                    _adaptiveSession.value = Resource.Success(response.data!!)
                } else {
                    _adaptiveSession.value = Resource.Error(response.message)
                }
            } catch (e: Exception) {
                _adaptiveSession.value = Resource.Error(e.message ?: "End failed")
            }
        }
    }

    fun loadStudentAnalytics(studentId: Long) {
        viewModelScope.launch {
            _studentAnalytics.value = Resource.Loading()
            _studentAnalytics.value = repository.getStudentAnalyticsDashboard(studentId)
        }
    }

    fun loadRecommendations(studentId: Long) {
        viewModelScope.launch {
            _recommendations.value = Resource.Loading()
            _recommendations.value = repository.getRecommendations(studentId)
        }
    }

    fun loadPredictions(studentId: Long) {
        viewModelScope.launch {
            _predictions.value = Resource.Loading()
            _predictions.value = repository.getPredictions(studentId)
        }
    }

    fun loadLeaderboard(limit: Int = 50) {
        viewModelScope.launch {
            _leaderboard.value = Resource.Loading()
            _leaderboard.value = repository.getStudentLeaderboard(limit)
        }
    }

    fun loadInsights(userId: Long) {
        viewModelScope.launch {
            _insights.value = Resource.Loading()
            _insights.value = repository.getInsights(userId)
        }
    }
}
