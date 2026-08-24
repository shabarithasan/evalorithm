package com.evalorithm.data.model

data class Certificate(
    val id: Long,
    val certificateType: String,
    val studentName: String,
    val registerNumber: String,
    val examTitle: String?,
    val subjectName: String?,
    val issuedDate: String,
    val certificateNumber: String,
    val qrCode: String,
    val issuedByName: String?,
    val digitalSignature: String?
)
