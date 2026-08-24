package com.evalorithm.data.api

import com.evalorithm.data.model.AIInsight
import com.evalorithm.data.model.AIQuestion
import com.evalorithm.data.model.AIDashboardData
import com.evalorithm.data.model.AdaptiveQuestion
import com.evalorithm.data.model.AdaptiveSession
import com.evalorithm.data.model.ApiResponse
import com.evalorithm.data.model.Attainment
import com.evalorithm.data.model.AuditLog
import com.evalorithm.data.model.AuthResponse
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
import com.evalorithm.data.model.LiveExamQuestion
import com.evalorithm.data.model.LoginRequest
import com.evalorithm.data.model.Notification
import com.evalorithm.data.model.PageResponse
import com.evalorithm.data.model.Prediction
import com.evalorithm.data.model.Recommendation
import com.evalorithm.data.model.RegisterRequest
import com.evalorithm.data.model.Semester
import com.evalorithm.data.model.StudentAnalytics
import com.evalorithm.data.model.StudentDashboardData
import com.evalorithm.data.model.SubmitExamResult
import com.evalorithm.data.model.Subject
import com.evalorithm.data.model.SupportTicket
import com.evalorithm.data.model.SystemSetting
import com.evalorithm.data.model.Question
import com.evalorithm.data.model.QuestionCategory
import com.evalorithm.data.model.QuestionDashboardData
import com.evalorithm.data.model.QuestionStatistics
import com.evalorithm.data.model.QuestionVersion
import com.evalorithm.data.model.UnitPerformanceItem
import com.evalorithm.data.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: Map<String, String>): ApiResponse<AuthResponse>

    @GET("departments")
    suspend fun getDepartments(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ApiResponse<PageResponse<Department>>

    @GET("departments/{id}")
    suspend fun getDepartment(@Path("id") id: Long): ApiResponse<Department>

    @GET("subjects")
    suspend fun getSubjects(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ApiResponse<PageResponse<Subject>>

    @GET("subjects/department/{departmentId}")
    suspend fun getSubjectsByDepartment(@Path("departmentId") deptId: Long): ApiResponse<List<Subject>>

    @GET("semesters")
    suspend fun getSemesters(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ApiResponse<PageResponse<Semester>>

    @GET("notifications")
    suspend fun getNotifications(): ApiResponse<List<Notification>>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): ApiResponse<Int>

    @PUT("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Long): ApiResponse<Unit>

    @PUT("notifications/read-all")
    suspend fun markAllNotificationsRead(): ApiResponse<Unit>

    @GET("admin/dashboard")
    suspend fun getAdminDashboard(): ApiResponse<DashboardData>

    @GET("faculty/dashboard")
    suspend fun getFacultyDashboard(): ApiResponse<FacultyDashboardData>

    @GET("student/dashboard")
    suspend fun getStudentDashboard(): ApiResponse<StudentDashboardData>

    @GET("profile")
    suspend fun getProfile(): ApiResponse<User>

    @PUT("profile")
    suspend fun updateProfile(@Body request: Map<String, String>): ApiResponse<User>

    // Question Categories
    @GET("question-categories")
    suspend fun getQuestionCategories(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): ApiResponse<PageResponse<QuestionCategory>>

    @POST("question-categories")
    suspend fun createQuestionCategory(@Body request: Map<String, String>): ApiResponse<QuestionCategory>

    // Questions
    @GET("questions")
    suspend fun getQuestions(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("departmentId") departmentId: Long? = null,
        @Query("semesterId") semesterId: Long? = null,
        @Query("subjectId") subjectId: Long? = null,
        @Query("questionType") questionType: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("status") status: String? = null,
        @Query("searchTerm") searchTerm: String? = null
    ): ApiResponse<PageResponse<Question>>

    @GET("questions/{id}")
    suspend fun getQuestion(@Path("id") id: Long): ApiResponse<Question>

    @POST("questions")
    suspend fun createQuestion(@Body request: Map<String, Any>): ApiResponse<Question>

    @PUT("questions/{id}")
    suspend fun updateQuestion(@Path("id") id: Long, @Body request: Map<String, Any>): ApiResponse<Question>

    @DELETE("questions/{id}")
    suspend fun deleteQuestion(@Path("id") id: Long): ApiResponse<Unit>

    @POST("questions/{id}/duplicate")
    suspend fun duplicateQuestion(@Path("id") id: Long): ApiResponse<Question>

    @PUT("questions/{id}/archive")
    suspend fun archiveQuestion(@Path("id") id: Long): ApiResponse<Unit>

    @PUT("questions/{id}/restore")
    suspend fun restoreQuestion(@Path("id") id: Long): ApiResponse<Unit>

    @PUT("questions/{id}/submit-review")
    suspend fun submitForReview(@Path("id") id: Long): ApiResponse<Unit>

    @PUT("questions/{id}/approve")
    suspend fun approveQuestion(
        @Path("id") id: Long,
        @Body request: Map<String, String>
    ): ApiResponse<Unit>

    @GET("questions/{id}/versions")
    suspend fun getQuestionVersions(@Path("id") id: Long): ApiResponse<List<QuestionVersion>>

    @GET("questions/dashboard")
    suspend fun getQuestionDashboard(): ApiResponse<QuestionDashboardData>

    // Question Statistics
    @GET("question-statistics/{questionId}")
    suspend fun getQuestionStatistics(@Path("questionId") questionId: Long): ApiResponse<QuestionStatistics>

    // Exams
    @GET("exams")
    suspend fun getExams(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("status") status: String? = null,
        @Query("examType") examType: String? = null
    ): ApiResponse<PageResponse<Exam>>

    @GET("exams/{id}")
    suspend fun getExam(@Path("id") id: Long): ApiResponse<Exam>

    @GET("exams/dashboard")
    suspend fun getExamDashboard(): ApiResponse<ExamDashboardData>

    // Exam Taking
    @POST("exam-session/start")
    suspend fun startExam(@Body request: Map<String, Long>): ApiResponse<Map<String, Any>>

    @GET("exam-session/{attemptId}/question/{index}")
    suspend fun getExamQuestion(
        @Path("attemptId") attemptId: Long,
        @Path("index") index: Int
    ): ApiResponse<LiveExamQuestion>

    @POST("exam-session/{attemptId}/save-answer")
    suspend fun saveExamAnswer(
        @Path("attemptId") attemptId: Long,
        @Body answer: Map<String, Any>
    ): ApiResponse<Unit>

    @POST("exam-session/{attemptId}/submit")
    suspend fun submitExam(@Path("attemptId") attemptId: Long): ApiResponse<SubmitExamResult>

    @POST("exam-session/{attemptId}/resume")
    suspend fun resumeExam(@Path("attemptId") attemptId: Long): ApiResponse<LiveExamQuestion>

    @GET("exam-session/{examId}/status")
    suspend fun getExamStatus(@Path("examId") examId: Long): ApiResponse<Map<String, Any>>

    // Results
    @GET("exam-results/student/{studentId}")
    suspend fun getStudentResults(
        @Path("studentId") studentId: Long,
        @Query("page") page: Int = 0
    ): ApiResponse<PageResponse<ExamResult>>

    @GET("exam-results/exam/{examId}/student/{studentId}")
    suspend fun getExamResult(
        @Path("examId") examId: Long,
        @Path("studentId") studentId: Long
    ): ApiResponse<ExamResult>

    // AI Questions
    @POST("ai/questions/generate")
    suspend fun generateAIQuestions(@Body request: Map<String, Any>): ApiResponse<List<AIQuestion>>

    @GET("ai/questions")
    suspend fun getAIQuestions(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("subjectId") subjectId: Long? = null
    ): ApiResponse<PageResponse<AIQuestion>>

    @PUT("ai/questions/{id}/approve")
    suspend fun approveAIQuestion(@Path("id") id: Long): ApiResponse<Unit>

    @GET("ai/questions/dashboard")
    suspend fun getAIDashboard(): ApiResponse<AIDashboardData>

    // Adaptive Testing
    @POST("adaptive/start")
    suspend fun startAdaptiveSession(@Body request: Map<String, Long>): ApiResponse<AdaptiveSession>

    @GET("adaptive/{sessionId}/next")
    suspend fun getNextAdaptiveQuestion(@Path("sessionId") sessionId: Long): ApiResponse<AdaptiveQuestion>

    @POST("adaptive/{sessionId}/answer")
    suspend fun submitAdaptiveAnswer(
        @Path("sessionId") sessionId: Long,
        @Body answer: Map<String, Any>
    ): ApiResponse<AdaptiveQuestion>

    @POST("adaptive/{sessionId}/end")
    suspend fun endAdaptiveSession(@Path("sessionId") sessionId: Long): ApiResponse<AdaptiveSession>

    // Analytics
    @GET("analytics/students/{studentId}/dashboard")
    suspend fun getStudentAnalyticsDashboard(@Path("studentId") studentId: Long): ApiResponse<StudentAnalytics>

    @GET("analytics/students/{studentId}/subjects")
    suspend fun getStudentSubjectPerformance(@Path("studentId") studentId: Long): ApiResponse<List<UnitPerformanceItem>>

    @GET("analytics/students/{studentId}/accuracy-trend")
    suspend fun getStudentAccuracyTrend(@Path("studentId") studentId: Long): ApiResponse<List<Map<String, Any>>>

    // Recommendations
    @POST("recommendations/generate/{studentId}")
    suspend fun generateRecommendations(@Path("studentId") studentId: Long): ApiResponse<List<Recommendation>>

    @GET("recommendations/{studentId}")
    suspend fun getRecommendations(@Path("studentId") studentId: Long): ApiResponse<List<Recommendation>>

    @PUT("recommendations/{id}/read")
    suspend fun markRecommendationRead(@Path("id") id: Long): ApiResponse<Unit>

    // Predictions
    @POST("predictions/generate")
    suspend fun generatePrediction(@Body request: Map<String, Long>): ApiResponse<List<Prediction>>

    @GET("predictions/student/{studentId}")
    suspend fun getPredictions(@Path("studentId") studentId: Long): ApiResponse<List<Prediction>>

    // Leaderboards
    @GET("leaderboards/students")
    suspend fun getStudentLeaderboard(@Query("limit") limit: Int = 50): ApiResponse<List<LeaderboardItem>>

    @GET("leaderboards/departments")
    suspend fun getDepartmentLeaderboard(): ApiResponse<List<LeaderboardItem>>

    // AI Insights
    @POST("ai/insights/generate/{userId}")
    suspend fun generateInsights(@Path("userId") userId: Long): ApiResponse<List<AIInsight>>

    @GET("ai/insights/{userId}")
    suspend fun getInsights(@Path("userId") userId: Long): ApiResponse<List<AIInsight>>

    // OBE - Course Outcomes
    @GET("co/subject/{subjectId}")
    suspend fun getCOsBySubject(@Path("subjectId") subjectId: Long): ApiResponse<List<CourseOutcome>>
    @POST("co")
    suspend fun createCO(@Body co: Map<String, Any>): ApiResponse<CourseOutcome>
    @PUT("co/{id}")
    suspend fun updateCO(@Path("id") id: Long, @Body co: Map<String, Any>): ApiResponse<CourseOutcome>
    @DELETE("co/{id}")
    suspend fun deleteCO(@Path("id") id: Long): ApiResponse<Unit>

    // Attainment
    @POST("attainment/calculate")
    suspend fun calculateAttainment(@Body request: Map<String, Any>): ApiResponse<Attainment>
    @GET("attainment/dashboard/{deptId}/{academicYear}")
    suspend fun getAttainmentDashboard(
        @Path("deptId") deptId: Long, @Path("academicYear") year: String
    ): ApiResponse<Map<String, Any>>
    @GET("attainment/subject/{subjectId}/{semesterId}")
    suspend fun getSubjectAttainment(
        @Path("subjectId") subjectId: Long, @Path("semesterId") semesterId: Long
    ): ApiResponse<List<Attainment>>

    // Certificates
    @POST("certificates/generate")
    suspend fun generateCertificate(@Body request: Map<String, Any>): ApiResponse<Certificate>
    @GET("certificates/student/{studentId}")
    suspend fun getStudentCertificates(@Path("studentId") studentId: Long): ApiResponse<List<Certificate>>
    @GET("certificates/verify/{certNumber}")
    suspend fun verifyCertificate(@Path("certNumber") certNumber: String): ApiResponse<Certificate>

    // Feedback
    @POST("feedback")
    suspend fun submitFeedback(@Body feedback: Map<String, Any>): ApiResponse<Feedback>
    @GET("feedback/received/{userId}")
    suspend fun getReceivedFeedback(@Path("userId") userId: Long): ApiResponse<List<Feedback>>
    @GET("feedback/given/{userId}")
    suspend fun getGivenFeedback(@Path("userId") userId: Long): ApiResponse<List<Feedback>>
    @GET("feedback/rating/{subjectId}")
    suspend fun getAverageRating(@Path("subjectId") subjectId: Long): ApiResponse<Double>

    // Support Tickets
    @POST("support-tickets")
    suspend fun createTicket(@Body ticket: Map<String, String>): ApiResponse<SupportTicket>
    @GET("support-tickets/my/{userId}")
    suspend fun getMyTickets(@Path("userId") userId: Long): ApiResponse<List<SupportTicket>>

    // Audit Logs
    @GET("audit-logs")
    suspend fun getAuditLogs(@Query("page") page: Int = 0, @Query("size") size: Int = 50): ApiResponse<PageResponse<AuditLog>>

    // System Settings
    @GET("system-settings")
    suspend fun getSystemSettings(): ApiResponse<List<SystemSetting>>
    @PUT("system-settings")
    suspend fun updateSystemSetting(@Body setting: Map<String, String>): ApiResponse<SystemSetting>

    // Backups
    @POST("backups/create")
    suspend fun createBackup(): ApiResponse<Backup>
    @GET("backups")
    suspend fun getBackups(): ApiResponse<List<Backup>>
    @POST("backups/{id}/restore")
    suspend fun restoreBackup(@Path("id") id: Long): ApiResponse<Unit>

    // Reports
    @POST("reports/generate")
    suspend fun generateReport(@Body request: Map<String, Any>): ApiResponse<Map<String, Any>>
}
