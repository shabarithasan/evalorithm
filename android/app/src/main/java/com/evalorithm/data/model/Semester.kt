package com.evalorithm.data.model

data class Semester(
    val id: Long,
    val number: Int,
    val departmentId: Long,
    val departmentName: String?,
    val status: String,
    val createdAt: String
)
