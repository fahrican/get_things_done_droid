package com.justluxurylifestyle.get_things_done_droid.model

import android.os.Parcelable
import android.view.View
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class TaskFetchResponse(
    val id: Long,
    val description: String,
    val isReminderSet: Boolean?,
    val isTaskOpen: Boolean?,
    val createdOn: String?,
    val priority: Priority?
) : Parcelable {
    var onClick: View.OnClickListener? = null
}