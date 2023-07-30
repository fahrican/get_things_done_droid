package com.justluxurylifestyle.get_things_done_droid.repository

import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
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
        private const val HTTP_EXCEPTION = "HTTP Exception"
        private const val SUCCESS_NO_CONTENT: Int = 204
    }

    override suspend fun getTasks(status: String?) = executeSafeApiCall {
        val taskStatus = TaskStatus.values().find { it.name == status }?.toString()
        taskApiService.getTasks(taskStatus)
    }

    override suspend fun getTaskById(id: String) = executeSafeApiCall {
        taskApiService.getTaskById(id)
    }

    override suspend fun createTask(createRequest: TaskCreateRequest) = executeSafeApiCall {
        taskApiService.createTask(createRequest)
    }

    override suspend fun canDeleteTask(id: String) = executeSafeApiCall {
        val response = taskApiService.canDeleteTask(id)
        if (response.code() != SUCCESS_NO_CONTENT) throw HttpException(response)
        response
    }

    override suspend fun updateTask(id: String, updateRequest: TaskUpdateRequest) =
        executeSafeApiCall {
            taskApiService.updateTaskWithId(id, updateRequest)
        }

    private suspend fun <T : Any> executeSafeApiCall(apiCall: suspend () -> T): ViewState<T> {
        return try {
            handleSuccess(apiCall.invoke())
        } catch (error: HttpException) {
            Timber.e("$HTTP_EXCEPTION: ${error.message}")
            handleException(error.code())
        } catch (error: Exception) {
            Timber.e("Unknown exception: ${error.message}")
            ViewState.Error(error)
        }
    }
}
