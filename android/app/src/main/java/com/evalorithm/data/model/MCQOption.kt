package com.evalorithm.data.model

data class MCQOption(
    val id: Long?,
    val optionLabel: String,
    val optionText: String,
    val isCorrect: Boolean,
    val explanation: String?
)
