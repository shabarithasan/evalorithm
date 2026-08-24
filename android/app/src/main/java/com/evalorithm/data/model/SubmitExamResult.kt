package com.evalorithm.data.model

data class SubmitExamResult(
    val attemptId: Long,
    val totalAnswered: Int,
    val totalCorrect: Int,
    val totalWrong: Int,
    val totalSkipped: Int,
    val autoEvaluated: Boolean,
    val message: String
)
