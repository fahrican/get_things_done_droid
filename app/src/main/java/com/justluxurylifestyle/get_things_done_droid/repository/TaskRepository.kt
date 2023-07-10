package com.justluxurylifestyle.get_things_done_droid.repository

import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.model.TaskUpdateRequest
import retrofit2.Response

interface TaskRepository {

    suspend fun getTasks(status: String?): ViewState<List<TaskFetchResponse>>

    suspend fun getTaskById(id: String): ViewState<TaskFetchResponse>

    suspend fun createTask(createRequest: TaskCreateRequest): ViewState<TaskFetchResponse>

    suspend fun deleteTask(id: String): ViewState<Response<Unit>>

    suspend fun updateTask(
        id: String,
        updateRequest: TaskUpdateRequest
    ): ViewState<TaskFetchResponse>
}