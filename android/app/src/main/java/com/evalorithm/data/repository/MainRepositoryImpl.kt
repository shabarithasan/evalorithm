package com.evalorithm.data.repository

import com.evalorithm.data.api.ApiInterface
import com.evalorithm.data.model.AIDashboardData
import com.evalorithm.data.model.AIInsight
import com.evalorithm.data.model.Attainment
import com.evalorithm.data.model.Backup
import com.evalorithm.data.model.Certificate
import com.evalorithm.data.model.CourseOutcome
import com.evalorithm.data.model.DashboardData
import com.evalorithm.data.model.Department
import com.evalorithm.data.model.Exam
import com.evalorithm.data.model.ExamDashboardData
import com.evalorithm.data.model.ExamResult
import com.evalorithm.data.model.FacultyDashboardData
import com.evalorithm.data.model.Feedback
import com.evalorithm.data.model.LeaderboardItem
import com.evalorithm.data.model.Notification
import com.evalorithm.data.model.PageResponse
import com.evalorithm.data.model.Prediction
import com.evalorithm.data.model.Recommendation
import com.evalorithm.data.model.Semester
import com.evalorithm.data.model.StudentAnalytics
import com.evalorithm.data.model.StudentDashboardData
import com.evalorithm.data.model.Subject
import com.evalorithm.data.model.SupportTicket
import com.evalorithm.data.model.SystemSetting
import com.evalorithm.data.model.Question
import com.evalorithm.data.model.QuestionCategory
import com.evalorithm.data.model.QuestionDashboardData
import com.evalorithm.data.model.QuestionStatistics
import com.evalorithm.data.model.QuestionVersion
import com.evalorithm.data.model.User
import com.evalorithm.util.Resource
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val api: ApiInterface
) : MainRepository {

    override suspend fun getDepartments(page: Int, size: Int): Resource<PageResponse<Department>> {
        return try {
            val response = api.getDepartments(page, size)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getSubjects(page: Int, size: Int): Resource<PageResponse<Subject>> {
        return try {
            val response = api.getSubjects(page, size)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getSemesters(page: Int, size: Int): Resource<PageResponse<Semester>> {
        return try {
            val response = api.getSemesters(page, size)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getNotifications(): Resource<List<Notification>> {
        return try {
            val response = api.getNotifications()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getUnreadCount(): Resource<Int> {
        return try {
            val response = api.getUnreadCount()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun markNotificationRead(id: Long): Resource<Unit> {
        return try {
            val response = api.markNotificationRead(id)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getAdminDashboard(): Resource<DashboardData> {
        return try {
            val response = api.getAdminDashboard()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getFacultyDashboard(): Resource<FacultyDashboardData> {
        return try {
            val response = api.getFacultyDashboard()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getStudentDashboard(): Resource<StudentDashboardData> {
        return try {
            val response = api.getStudentDashboard()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getProfile(): Resource<User> {
        return try {
            val response = api.getProfile()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getQuestionCategories(page: Int, size: Int): Resource<PageResponse<QuestionCategory>> {
        return try {
            val response = api.getQuestionCategories(page, size)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun createQuestionCategory(request: Map<String, String>): Resource<QuestionCategory> {
        return try {
            val response = api.createQuestionCategory(request)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getQuestions(
        page: Int, size: Int, departmentId: Long?, semesterId: Long?,
        subjectId: Long?, questionType: String?, difficulty: String?,
        status: String?, searchTerm: String?
    ): Resource<PageResponse<Question>> {
        return try {
            val response = api.getQuestions(page, size, departmentId, semesterId, subjectId, questionType, difficulty, status, searchTerm)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getQuestion(id: Long): Resource<Question> {
        return try {
            val response = api.getQuestion(id)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun createQuestion(request: Map<String, Any>): Resource<Question> {
        return try {
            val response = api.createQuestion(request)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun updateQuestion(id: Long, request: Map<String, Any>): Resource<Question> {
        return try {
            val response = api.updateQuestion(id, request)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun deleteQuestion(id: Long): Resource<Unit> {
        return try {
            val response = api.deleteQuestion(id)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun duplicateQuestion(id: Long): Resource<Question> {
        return try {
            val response = api.duplicateQuestion(id)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun archiveQuestion(id: Long): Resource<Unit> {
        return try {
            val response = api.archiveQuestion(id)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun restoreQuestion(id: Long): Resource<Unit> {
        return try {
            val response = api.restoreQuestion(id)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun submitForReview(id: Long): Resource<Unit> {
        return try {
            val response = api.submitForReview(id)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun approveQuestion(id: Long, status: String, comments: String): Resource<Unit> {
        return try {
            val response = api.approveQuestion(id, mapOf("status" to status, "comments" to comments))
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getQuestionVersions(id: Long): Resource<List<QuestionVersion>> {
        return try {
            val response = api.getQuestionVersions(id)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getQuestionDashboard(): Resource<QuestionDashboardData> {
        return try {
            val response = api.getQuestionDashboard()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getQuestionStatistics(questionId: Long): Resource<QuestionStatistics> {
        return try {
            val response = api.getQuestionStatistics(questionId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getExams(page: Int, size: Int, status: String?, examType: String?): Resource<PageResponse<Exam>> {
        return try {
            val response = api.getExams(page, size, status, examType)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getExam(id: Long): Resource<Exam> {
        return try {
            val response = api.getExam(id)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getExamDashboard(): Resource<ExamDashboardData> {
        return try {
            val response = api.getExamDashboard()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getStudentResults(studentId: Long, page: Int): Resource<PageResponse<ExamResult>> {
        return try {
            val response = api.getStudentResults(studentId, page)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getAIDashboard(): Resource<AIDashboardData> {
        return try {
            val response = api.getAIDashboard()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getStudentAnalyticsDashboard(studentId: Long): Resource<StudentAnalytics> {
        return try {
            val response = api.getStudentAnalyticsDashboard(studentId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getRecommendations(studentId: Long): Resource<List<Recommendation>> {
        return try {
            val response = api.getRecommendations(studentId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getPredictions(studentId: Long): Resource<List<Prediction>> {
        return try {
            val response = api.getPredictions(studentId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getStudentLeaderboard(limit: Int): Resource<List<LeaderboardItem>> {
        return try {
            val response = api.getStudentLeaderboard(limit)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getInsights(userId: Long): Resource<List<AIInsight>> {
        return try {
            val response = api.getInsights(userId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getCOsBySubject(subjectId: Long): Resource<List<CourseOutcome>> {
        return try {
            val response = api.getCOsBySubject(subjectId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getSubjectAttainment(subjectId: Long, semesterId: Long): Resource<List<Attainment>> {
        return try {
            val response = api.getSubjectAttainment(subjectId, semesterId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getStudentCertificates(studentId: Long): Resource<List<Certificate>> {
        return try {
            val response = api.getStudentCertificates(studentId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getReceivedFeedback(userId: Long): Resource<List<Feedback>> {
        return try {
            val response = api.getReceivedFeedback(userId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getMyTickets(userId: Long): Resource<List<SupportTicket>> {
        return try {
            val response = api.getMyTickets(userId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getSystemSettings(): Resource<List<SystemSetting>> {
        return try {
            val response = api.getSystemSettings()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getBackups(): Resource<List<Backup>> {
        return try {
            val response = api.getBackups()
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
