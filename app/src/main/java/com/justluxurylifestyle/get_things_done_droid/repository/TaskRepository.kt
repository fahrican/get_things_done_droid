package com.justluxurylifestyle.get_things_done_droid.repository

import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem

interface TaskRepository {

    suspend fun getTasks(endpoint: String): ViewState<List<TaskResponseItem>>

    suspend fun createTask(task: TaskResponseItem): ViewState<TaskResponseItem>

    suspend fun deleteTask(id: String): ViewState<String>

    suspend fun updateTask(task: TaskResponseItem): ViewState<TaskResponseItem>
}