package com.justluxurylifestyle.get_things_done_droid.networking

import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem
import retrofit2.http.GET

interface TaskApi {
    companion object {
        const val TASK_API_BASE_URL = "https://justluxurylifestyle.com/"
    }

    @GET("api/open-tasks")
    suspend fun getOpenTasks(): List<TaskResponseItem>
}