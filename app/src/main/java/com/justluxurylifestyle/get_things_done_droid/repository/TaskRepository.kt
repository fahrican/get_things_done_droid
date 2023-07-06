package com.justluxurylifestyle.get_things_done_droid.repository

import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse

interface TaskRepository {

    suspend fun getTasks(endpoint: String?): ViewState<List<TaskFetchResponse>>

    suspend fun createTask(taskRequest: TaskCreateRequest): ViewState<TaskFetchResponse>

    suspend fun deleteTask(id: String): ViewState<Unit>

    suspend fun updateTask(task: TaskFetchResponse): ViewState<TaskFetchResponse>
}