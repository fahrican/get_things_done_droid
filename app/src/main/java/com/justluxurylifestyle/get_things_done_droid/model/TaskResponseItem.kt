package com.justluxurylifestyle.get_things_done_droid.model

data class TaskResponseItem(
    val id: Int,
    val description: String,
    val createdOn: String?,
    val finishedOn: String?,
    val isReminderSet: Boolean?,
    val isTaskOpen: Boolean?,
    val priority: String?,
    val startedOn: String?,
    val timeInterval: String?,
    val timeTaken: Int?
)