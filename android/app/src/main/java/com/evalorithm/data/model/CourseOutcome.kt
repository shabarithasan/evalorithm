package com.evalorithm.data.model

data class CourseOutcome(
    val id: Long,
    val code: String,
    val description: String?,
    val subjectName: String?,
    val departmentName: String?,
    val semesterNumber: Int?,
    val bloomsLevel: String?,
    val isAttainable: Boolean,
    val mappingCount: Int
)
