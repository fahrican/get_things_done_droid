package com.justluxurylifestyle.get_things_done_droid.ui.open_task

import com.justluxurylifestyle.get_things_done_droid.model.TaskResponse
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class OpenTaskRepositoryImpl @Inject constructor(
    private val taskApiService: TaskApi
) : OpenTaskRepository {

    override suspend fun getOpenTasks(): Result<List<TaskResponseItem>> {
        var result: Result<List<TaskResponseItem>>
        try {
            val response = taskApiService.getOpenTasks()
            response.let { result = Result.success(it) }
        } catch (error: HttpException) {
            Timber.e("HttpException: ${error.message}")
            return Result.failure(error)
        }
        return result
    }
}