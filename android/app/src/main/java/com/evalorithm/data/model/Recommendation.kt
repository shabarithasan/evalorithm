package com.evalorithm.data.model

data class Recommendation(
    val id: Long,
    val type: String,
    val title: String,
    val description: String,
    val priority: String,
    val subjectName: String?,
    val topicName: String?,
    val isRead: Boolean,
    val generatedAt: String
)
