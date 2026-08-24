package com.evalorithm.data.model

data class Feedback(
    val id: Long,
    val feedbackType: String,
    val fromUserName: String,
    val toUserName: String?,
    val subjectName: String?,
    val rating: Int,
    val comment: String,
    val suggestions: String?,
    val isAnonymous: Boolean,
    val createdAt: String
)
