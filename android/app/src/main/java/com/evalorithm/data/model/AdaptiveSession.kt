package com.evalorithm.data.model

data class AdaptiveSession(
    val id: Long,
    val subjectName: String,
    val currentDifficulty: String,
    val questionsAnswered: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val accuracy: Double,
    val streakCount: Int,
    val maxStreak: Int,
    val isActive: Boolean,
    val score: Double
)
