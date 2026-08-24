package com.evalorithm.data.model

data class LeaderboardItem(
    val rank: Int,
    val studentId: Long?,
    val studentName: String?,
    val departmentName: String?,
    val score: Double,
    val accuracy: Double,
    val totalExams: Int
)
