package com.evalorithm.data.model

data class ExamResult(
    val id: Long,
    val examTitle: String,
    val studentName: String,
    val totalMarksObtained: Double,
    val totalMarksPossible: Int,
    val percentage: Double,
    val grade: String,
    val isPassed: Boolean,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val skippedQuestions: Int,
    val timeTakenMinutes: Int,
    val evaluatedAt: String?
)
