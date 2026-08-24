package com.evalorithm.data.model

data class QuestionVersion(
    val id: Long,
    val versionNumber: Int,
    val updatedByName: String,
    val changeDescription: String?,
    val createdAt: String
)
