package com.justluxurylifestyle.get_things_done_droid.ui.open_task

import com.justluxurylifestyle.get_things_done_droid.model.TaskResponse
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem

interface OpenTaskRepository {

    suspend fun getOpenTasks(): Result<List<TaskResponseItem>>
}