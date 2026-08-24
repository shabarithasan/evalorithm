package com.evalorithm.data.model

data class QuestionDashboardData(
    val totalQuestions: Int,
    val approvedQuestions: Int,
    val pendingQuestions: Int,
    val rejectedQuestions: Int,
    val recentlyAdded: Int
)
