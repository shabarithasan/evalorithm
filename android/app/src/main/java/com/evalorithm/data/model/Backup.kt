package com.evalorithm.data.model

data class Backup(
    val id: Long,
    val fileName: String,
    val fileSize: Long,
    val backupType: String,
    val status: String,
    val createdByName: String?,
    val createdAt: String
)
