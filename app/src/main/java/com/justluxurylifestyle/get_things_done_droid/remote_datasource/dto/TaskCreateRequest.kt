package com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto


data class TaskCreateRequest(
    val description: String,
    val isReminderSet: Boolean,
    val isTaskOpen: Boolean,
    val timeInterval: String?,
    val priority: Priority
)