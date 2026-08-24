package com.evalorithm.data.model

data class Prediction(
    val id: Long,
    val subjectName: String,
    val predictedMarks: Double,
    val predictedGrade: String,
    val passProbability: Double,
    val riskLevel: String,
    val suggestedImprovement: String,
    val confidenceLevel: Double,
    val generatedAt: String
)
