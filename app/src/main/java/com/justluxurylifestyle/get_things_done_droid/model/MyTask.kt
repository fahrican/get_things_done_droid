package com.justluxurylifestyle.get_things_done_droid.model

import android.view.View

data class MyTask(val task: TaskResponseItem?) {

    var onClick: View.OnClickListener? = null
}

