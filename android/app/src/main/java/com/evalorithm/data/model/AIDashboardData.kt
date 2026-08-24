package com.evalorithm.data.model

data class AIDashboardData(
    val aiGeneratedQuestions: Int,
    val adaptiveExams: Int,
    val studentPerformance: Double,
    val weakTopicsCount: Int,
    val strongTopicsCount: Int,
    val recommendationsCount: Int
)
