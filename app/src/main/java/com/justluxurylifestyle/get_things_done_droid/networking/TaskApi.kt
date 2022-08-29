package com.justluxurylifestyle.get_things_done_droid.networking

import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem
import retrofit2.http.*

interface TaskApi {
    companion object {
        const val TASK_API_BASE_URL = "https://justluxurylifestyle.com/"
        const val OPEN_TASKS = "open-tasks"
        const val ALL_TASKS = "all-tasks"
        const val CLOSED_TASKS = "closed-tasks"
        const val REQUEST_SUCCESS = "Request was successful!"
        const val REQUEST_FAILURE = "Request couldn't be processed!"
    }

    @GET("api/{endpoint}")
    suspend fun getTasks(@Path("endpoint") endpoint: String): List<TaskResponseItem>

    @POST("api/create")
    suspend fun createTask(@Body task: TaskResponseItem): TaskResponseItem

    @DELETE("api/delete/{id}")
    suspend fun deleteTask(@Path("id") id: String): String

    @PUT("api/update")
    suspend fun updateTask(@Body task: TaskResponseItem): TaskResponseItem

    @PUT("api/update/{id}")
    suspend fun updateTaskWithId(
        @Path("id") id: String,
        @Body task: TaskResponseItem
    ): TaskResponseItem
}