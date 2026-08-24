package com.evalorithm.data.model

data class AIInsight(
    val id: Long,
    val insightType: String,
    val title: String,
    val description: String,
    val subjectName: String?,
    val value: Double,
    val generatedAt: String
)
