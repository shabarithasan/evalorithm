package com.evalorithm.data.model

data class StudentAnalytics(
    val studentId: Long,
    val studentName: String,
    val subjectName: String,
    val totalAttempted: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val accuracy: Double,
    val averageScore: Double,
    val completionRate: Double,
    val avgTimePerQuestion: Double,
    val difficultyPerformance: Map<String, Double>,
    val unitPerformance: List<UnitPerformanceItem>,
    val topicPerformance: List<TopicPerformanceItem>
)

data class UnitPerformanceItem(
    val unitName: String,
    val subjectName: String,
    val accuracy: Double,
    val totalQuestions: Int
)

data class TopicPerformanceItem(
    val topicName: String,
    val unitName: String,
    val accuracy: Double,
    val totalQuestions: Int
)
