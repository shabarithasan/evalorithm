package com.evalorithm.data.model

data class QuestionCategory(
    val id: Long,
    val categoryName: String,
    val description: String?,
    val status: String,
    val questionCount: Int,
    val createdAt: String
)
