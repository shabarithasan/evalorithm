package com.evalorithm.data.model

data class QuestionStatistics(
    val id: Long,
    val viewCount: Int,
    val usageCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val correctPercentage: Double,
    val wrongPercentage: Double,
    val lastUsedAt: String?
)
