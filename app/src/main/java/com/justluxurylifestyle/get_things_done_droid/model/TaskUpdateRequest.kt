package com.justluxurylifestyle.get_things_done_droid.model

import java.time.LocalDateTime

data class TaskUpdateRequest(
    val description: String?,
    val isReminderSet: Boolean?,
    val isTaskOpen: Boolean?,
    val startedOn: String?,
    val finishedOn: String?,
    val timeInterval: String?,
    val timeTaken: Int?,
    val priority: Priority?
)