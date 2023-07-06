package com.justluxurylifestyle.get_things_done_droid.repository

import com.justluxurylifestyle.get_things_done_droid.core.BaseRepository
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.model.TaskStatus
import com.justluxurylifestyle.get_things_done_droid.model.TaskUpdateRequest
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskApiService: TaskApi
) : BaseRepository(), TaskRepository {

    companion object {
        const val HTTP_EXCEPTION = "HttpException"
    }

    override suspend fun getTasks(endpoint: String?): ViewState<List<TaskFetchResponse>> {
        var result: ViewState<List<TaskFetchResponse>>
        try {
            val response = when (endpoint) {
                TaskStatus.OPEN.toString() -> taskApiService.getTasks(TaskStatus.OPEN.toString())
                TaskStatus.CLOSED.toString() -> taskApiService.getTasks(TaskStatus.CLOSED.toString())
                else -> taskApiService.getTasks(null)
            }
            response.let { result = handleSuccess(it) }
        } catch (error: HttpException) {
            Timber.e("$HTTP_EXCEPTION: ${error.message}")
            return handleException(error.code())
        }
        return result
    }

    override suspend fun createTask(createRequest: TaskCreateRequest): ViewState<TaskFetchResponse> {
        var result: ViewState<TaskFetchResponse>
        try {
            val response = taskApiService.createTask(createRequest)
            response.let { result = handleSuccess(it) }
        } catch (error: HttpException) {
            Timber.e("$HTTP_EXCEPTION: ${error.message}")
            return handleException(error.code())
        }
        return result
    }

    override suspend fun deleteTask(id: String): ViewState<Unit> {
        var result: ViewState<Unit>
        try {
            val response = taskApiService.deleteTask(id)
            response.let { result = handleSuccess(it) }
        } catch (error: HttpException) {
            Timber.e("$HTTP_EXCEPTION: ${error.message}")
            return handleException(error.code())
        }
        return result
    }

    override suspend fun updateTask(
        id: String,
        updateRequest: TaskUpdateRequest
    ): ViewState<TaskFetchResponse> {
        var result: ViewState<TaskFetchResponse>
        try {
            val response = taskApiService.updateTaskWithId(id, updateRequest)
            response.let { result = handleSuccess(it) }
        } catch (error: HttpException) {
            Timber.e("$HTTP_EXCEPTION: ${error.message}")
            return handleException(error.code())
        }
        return result
    }
}