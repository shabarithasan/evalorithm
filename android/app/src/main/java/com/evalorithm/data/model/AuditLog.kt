package com.evalorithm.data.model

data class AuditLog(
    val id: Long,
    val userName: String,
    val action: String,
    val entityName: String,
    val description: String,
    val ipAddress: String,
    val timestamp: String
)
