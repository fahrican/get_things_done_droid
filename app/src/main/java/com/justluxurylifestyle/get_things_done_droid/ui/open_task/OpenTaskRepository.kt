package com.justluxurylifestyle.get_things_done_droid.ui.open_task

import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem

interface OpenTaskRepository {

    suspend fun getTasks(endpoint: String): ViewState<List<TaskResponseItem>>
}