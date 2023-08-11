package com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto

import android.os.Parcelable
import android.view.View
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskFetchResponse(
    val id: Long,
    val description: String,
    val isReminderSet: Boolean?,
    val isTaskOpen: Boolean?,
    val createdOn: String?,
    val startedOn: String?,
    val finishedOn: String?,
    val timeInterval: String?,
    val timeTaken: Int?,
    val priority: Priority?
) : Parcelable {

    @IgnoredOnParcel
    var onClick: View.OnClickListener? = null
}