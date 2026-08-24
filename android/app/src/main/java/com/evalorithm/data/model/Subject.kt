package com.evalorithm.data.model

data class Subject(
    val id: Long,
    val code: String,
    val name: String,
    val departmentId: Long,
    val departmentName: String?,
    val semesterId: Long,
    val semesterNumber: Int?,
    val credits: Int,
    val description: String?,
    val status: String,
    val createdAt: String
)
