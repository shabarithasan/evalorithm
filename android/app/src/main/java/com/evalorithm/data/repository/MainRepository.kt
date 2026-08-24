package com.evalorithm.data.repository

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

interface MainRepository {
    suspend fun getDepartments(page: Int, size: Int): Resource<PageResponse<Department>>
    suspend fun getSubjects(page: Int, size: Int): Resource<PageResponse<Subject>>
    suspend fun getSemesters(page: Int, size: Int): Resource<PageResponse<Semester>>
    suspend fun getNotifications(): Resource<List<Notification>>
    suspend fun getUnreadCount(): Resource<Int>
    suspend fun markNotificationRead(id: Long): Resource<Unit>
    suspend fun getAdminDashboard(): Resource<DashboardData>
    suspend fun getFacultyDashboard(): Resource<FacultyDashboardData>
    suspend fun getStudentDashboard(): Resource<StudentDashboardData>
    suspend fun getProfile(): Resource<User>

    // Question Categories
    suspend fun getQuestionCategories(page: Int, size: Int): Resource<PageResponse<QuestionCategory>>
    suspend fun createQuestionCategory(request: Map<String, String>): Resource<QuestionCategory>

    // Questions
    suspend fun getQuestions(
        page: Int, size: Int, departmentId: Long?, semesterId: Long?,
        subjectId: Long?, questionType: String?, difficulty: String?,
        status: String?, searchTerm: String?
    ): Resource<PageResponse<Question>>
    suspend fun getQuestion(id: Long): Resource<Question>
    suspend fun createQuestion(request: Map<String, Any>): Resource<Question>
    suspend fun updateQuestion(id: Long, request: Map<String, Any>): Resource<Question>
    suspend fun deleteQuestion(id: Long): Resource<Unit>
    suspend fun duplicateQuestion(id: Long): Resource<Question>
    suspend fun archiveQuestion(id: Long): Resource<Unit>
    suspend fun restoreQuestion(id: Long): Resource<Unit>
    suspend fun submitForReview(id: Long): Resource<Unit>
    suspend fun approveQuestion(id: Long, status: String, comments: String): Resource<Unit>
    suspend fun getQuestionVersions(id: Long): Resource<List<QuestionVersion>>
    suspend fun getQuestionDashboard(): Resource<QuestionDashboardData>
    suspend fun getQuestionStatistics(questionId: Long): Resource<QuestionStatistics>

    // Exams
    suspend fun getExams(page: Int, size: Int, status: String?, examType: String?): Resource<PageResponse<Exam>>
    suspend fun getExam(id: Long): Resource<Exam>
    suspend fun getExamDashboard(): Resource<ExamDashboardData>
    suspend fun getStudentResults(studentId: Long, page: Int): Resource<PageResponse<ExamResult>>

    // AI Engine
    suspend fun getAIDashboard(): Resource<AIDashboardData>
    suspend fun getStudentAnalyticsDashboard(studentId: Long): Resource<StudentAnalytics>
    suspend fun getRecommendations(studentId: Long): Resource<List<Recommendation>>
    suspend fun getPredictions(studentId: Long): Resource<List<Prediction>>
    suspend fun getStudentLeaderboard(limit: Int): Resource<List<LeaderboardItem>>
    suspend fun getInsights(userId: Long): Resource<List<AIInsight>>

    // OBE
    suspend fun getCOsBySubject(subjectId: Long): Resource<List<CourseOutcome>>
    suspend fun getSubjectAttainment(subjectId: Long, semesterId: Long): Resource<List<Attainment>>

    // Certificates
    suspend fun getStudentCertificates(studentId: Long): Resource<List<Certificate>>

    // Feedback
    suspend fun getReceivedFeedback(userId: Long): Resource<List<Feedback>>

    // Support Tickets
    suspend fun getMyTickets(userId: Long): Resource<List<SupportTicket>>

    // Settings
    suspend fun getSystemSettings(): Resource<List<SystemSetting>>

    // Backups
    suspend fun getBackups(): Resource<List<Backup>>
}
