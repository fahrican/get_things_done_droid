package com.justluxurylifestyle.get_things_done_droid.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskResponseItem(
    val id: Long? = null,
    val description: String? = null,
    val createdOn: String? = null,
    val finishedOn: String? = null,
    val isReminderSet: Boolean? = null,
    val isTaskOpen: Boolean? = null,
    val priority: Priority? = null,
    val startedOn: String? = null,
    val timeInterval: String? = null,
    val timeTaken: Int? = null
) : Parcelable