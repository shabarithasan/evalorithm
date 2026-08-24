package com.evalorithm.data.model

data class DashboardData(
    val totalDepartments: Int,
    val totalSubjects: Int,
    val totalFaculty: Int,
    val totalStudents: Int,
    val totalQuestions: Int
)

data class FacultyDashboardData(
    val assignedSubjects: Int,
    val questionCount: Int,
    val pendingQuestions: Int
)

data class StudentDashboardData(
    val enrolledSubjects: Int,
    val upcomingExams: Int,
    val recentResults: List<Any>
)
