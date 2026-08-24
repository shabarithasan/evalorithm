package com.evalorithm.data.model

data class SystemSetting(
    val id: Long,
    val settingKey: String,
    val settingValue: String,
    val category: String,
    val description: String?,
    val dataType: String?
)
