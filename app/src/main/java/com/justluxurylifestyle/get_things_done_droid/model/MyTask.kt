package com.justluxurylifestyle.get_things_done_droid.model

import android.view.View
import com.airbnb.epoxy.EpoxyModel
import com.justluxurylifestyle.get_things_done_droid.R

data class MyTask(val task: TaskResponseItem?): EpoxyModel<MyTask>() {

    var onClick: View.OnClickListener? = null

    override fun getDefaultLayout(): Int = R.layout.epoxy_item_task
}

