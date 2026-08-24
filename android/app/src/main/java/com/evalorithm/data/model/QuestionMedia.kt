package com.evalorithm.data.model

data class QuestionMedia(
    val id: Long,
    val fileName: String,
    val fileUrl: String,
    val fileType: String,
    val fileSize: Long,
    val uploadedAt: String
)
