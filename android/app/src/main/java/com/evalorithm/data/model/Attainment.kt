package com.evalorithm.data.model

data class Attainment(
    val id: Long,
    val coCode: String,
    val coDescription: String?,
    val subjectName: String?,
    val semesterNumber: Int?,
    val academicYear: String?,
    val targetAttainment: Double,
    val actualAttainment: Double,
    val directAttainment: Double,
    val indirectAttainment: Double,
    val isAchieved: Boolean
)
