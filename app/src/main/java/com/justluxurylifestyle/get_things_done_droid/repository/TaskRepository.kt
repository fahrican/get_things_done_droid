package com.justluxurylifestyle.get_things_done_droid.repository

import com.justluxurylifestyle.get_things_done_droid.core.StateOfView
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskUpdateRequest
import retrofit2.Response

interface TaskRepository {

    suspend fun getTasks(status: String?): StateOfView<List<TaskFetchResponse>>

    suspend fun getTaskById(id: String): StateOfView<TaskFetchResponse>

    suspend fun createTask(createRequest: TaskCreateRequest): StateOfView<TaskFetchResponse>

    suspend fun canDeleteTask(id: String): StateOfView<Response<Boolean>>

    suspend fun updateTask(
        id: String,
        updateRequest: TaskUpdateRequest
    ): StateOfView<TaskFetchResponse>
}