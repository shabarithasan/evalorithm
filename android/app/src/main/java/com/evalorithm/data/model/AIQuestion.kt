package com.evalorithm.data.model

data class AIQuestion(
    val id: Long,
    val questionText: String,
    val questionType: String,
    val difficulty: String,
    val bloomLevel: String,
    val options: List<AIQuestionOption>?,
    val correctAnswer: String?,
    val explanation: String?,
    val subjectName: String?,
    val unitName: String?,
    val topicName: String?,
    val isApproved: Boolean,
    val confidenceScore: Double,
    val createdAt: String
)

data class AIQuestionOption(
    val label: String,
    val text: String
)
