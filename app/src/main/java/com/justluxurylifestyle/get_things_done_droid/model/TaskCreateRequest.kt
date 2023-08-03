package com.justluxurylifestyle.get_things_done_droid.model


data class TaskCreateRequest(
    val description: String,
    val isReminderSet: Boolean,
    val isTaskOpen: Boolean,
    val timeInterval: String?,
    val priority: Priority
)