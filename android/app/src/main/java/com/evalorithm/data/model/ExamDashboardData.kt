package com.evalorithm.data.model

data class ExamDashboardData(
    val totalExams: Int,
    val activeExams: Int,
    val scheduledExams: Int,
    val completedExams: Int,
    val draftExams: Int
)
