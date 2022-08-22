package com.justluxurylifestyle.get_things_done_droid.model

import android.os.Parcel
import android.os.Parcelable

data class TaskResponseItem(
    val id: Int? = null,
    val description: String? = null,
    val createdOn: String? = null,
    val finishedOn: String? = null,
    val isReminderSet: Boolean? = null,
    val isTaskOpen: Boolean? = null,
    val priority: String? = null,
    val startedOn: String? = null,
    val timeInterval: String? = null,
    val timeTaken: Int? = null
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(description)
        parcel.writeString(createdOn)
        parcel.writeString(finishedOn)
        parcel.writeValue(isReminderSet)
        parcel.writeValue(isTaskOpen)
        parcel.writeString(priority)
        parcel.writeString(startedOn)
        parcel.writeString(timeInterval)
        parcel.writeValue(timeTaken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskResponseItem> {
        override fun createFromParcel(parcel: Parcel): TaskResponseItem {
            return TaskResponseItem(parcel)
        }

        override fun newArray(size: Int): Array<TaskResponseItem?> {
            return arrayOfNulls(size)
        }
    }
}