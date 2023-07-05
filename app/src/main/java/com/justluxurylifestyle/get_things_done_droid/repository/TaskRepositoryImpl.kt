package com.justluxurylifestyle.get_things_done_droid.repository

import com.justluxurylifestyle.get_things_done_droid.core.BaseRepository
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.model.TaskStatus
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

    override suspend fun createTask(task: TaskFetchResponse): ViewState<TaskFetchResponse> {
        var result: ViewState<TaskFetchResponse>
        try {
            val response = taskApiService.createTask(task)
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

    override suspend fun updateTask(task: TaskFetchResponse): ViewState<TaskFetchResponse> {
        var result: ViewState<TaskFetchResponse>
        try {
            val response = taskApiService.updateTaskWithId(task.id.toString(), task)
            response.let { result = handleSuccess(it) }
        } catch (error: HttpException) {
            Timber.e("$HTTP_EXCEPTION: ${error.message}")
            return handleException(error.code())
        }
        return result
    }
}