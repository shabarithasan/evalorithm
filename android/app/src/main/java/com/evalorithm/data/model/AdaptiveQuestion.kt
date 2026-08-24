package com.evalorithm.data.model

data class AdaptiveQuestion(
    val questionId: Long,
    val questionText: String,
    val questionType: String,
    val difficulty: String,
    val options: List<ExamOption>?,
    val marks: Int,
    val timeLimit: Int
)

data class ExamOption(
    val label: String,
    val text: String
)
