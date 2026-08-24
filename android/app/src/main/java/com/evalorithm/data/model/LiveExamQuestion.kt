package com.evalorithm.data.model

data class LiveExamQuestion(
    val examQuestionId: Long,
    val questionIndex: Int,
    val questionTitle: String,
    val questionDescription: String?,
    val questionType: String,
    val marks: Int,
    val orderNumber: Int,
    val options: List<ExamOption>?,
    val totalQuestions: Int,
    val userAnswer: StudentAnswerData?
)

data class ExamOption(
    val optionLabel: String,
    val optionText: String
)

data class StudentAnswerData(
    val examQuestionId: Long,
    val selectedOptionLabel: String?,
    val textAnswer: String?,
    val answerStatus: String
)
