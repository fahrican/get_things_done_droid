package com.justluxurylifestyle.get_things_done_droid.model

data class TaskResponseItem(
    val createdOn: String,
    val description: String,
    val finishedOn: String,
    val id: Int,
    val isReminderSet: Boolean,
    val isTaskOpen: Boolean,
    val priority: String,
    val startedOn: String,
    val timeInterval: String,
    val timeTaken: Int
)