package com.justluxurylifestyle.get_things_done_droid.repository

import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem

interface TaskRepository {

    suspend fun getTasks(endpoint: String): ViewState<List<TaskResponseItem>>

    suspend fun createTask(task: TaskResponseItem): TaskResponseItem
}