package com.justluxurylifestyle.get_things_done_droid.repository

import com.justluxurylifestyle.get_things_done_droid.core.BaseRepository
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem
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

    override suspend fun getTasks(endpoint: String): ViewState<List<TaskResponseItem>> {
        var result: ViewState<List<TaskResponseItem>>
        try {
            val response = when (endpoint) {
                TaskApi.OPEN_TASKS -> taskApiService.getTasks(TaskApi.OPEN_TASKS)
                TaskApi.CLOSED_TASKS -> taskApiService.getTasks(TaskApi.CLOSED_TASKS)
                else -> taskApiService.getTasks(TaskApi.ALL_TASKS)
            }
            response.let { result = handleSuccess(it) }
        } catch (error: HttpException) {
            Timber.e("$HTTP_EXCEPTION: ${error.message}")
            return handleException(error.code())
        }
        return result
    }

    override suspend fun createTask(task: TaskResponseItem): ViewState<TaskResponseItem> {
        var result: ViewState<TaskResponseItem>
        try {
            val response = taskApiService.createTask(task)
            response.let { result = handleSuccess(it) }
        } catch (error: HttpException) {
            Timber.e("$HTTP_EXCEPTION: ${error.message}")
            return handleException(error.code())
        }
        return result
    }

    override suspend fun deleteTask(id: String): ViewState<String> {
        var result: ViewState<String>
        try {
            val response = taskApiService.deleteTask(id)
            response.let { result = handleSuccess(it) }
        } catch (error: HttpException) {
            Timber.e("$HTTP_EXCEPTION: ${error.message}")
            return handleException(error.code())
        }
        return result
    }

    override suspend fun updateTask(task: TaskResponseItem): ViewState<TaskResponseItem> {
        var result: ViewState<TaskResponseItem>
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