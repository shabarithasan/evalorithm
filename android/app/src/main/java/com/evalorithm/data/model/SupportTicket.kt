package com.evalorithm.data.model

data class SupportTicket(
    val id: Long,
    val subject: String,
    val description: String,
    val status: String,
    val priority: String,
    val assignedToName: String?,
    val resolution: String?,
    val createdAt: String
)
