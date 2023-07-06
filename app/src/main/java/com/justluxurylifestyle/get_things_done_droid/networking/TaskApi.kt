package com.justluxurylifestyle.get_things_done_droid.networking

import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskApi {
    companion object {
        const val TASK_API_BASE_URL = "https://backend4frontend.onrender.com/"
        const val TASK_API_TASKS_ENDPOINT = "api/v1/tasks"
        const val TASK_API_TASK_ID_ENDPOINT = "api/v1/tasks/{id}"
        const val REQUEST_SUCCESS = "Request was successful!"
        const val REQUEST_FAILURE = "Request couldn't be processed!"
    }

    @GET(TASK_API_TASKS_ENDPOINT)
    suspend fun getTasks(@Query("status") status: String?): List<TaskFetchResponse>

    @POST(TASK_API_TASKS_ENDPOINT)
    suspend fun createTask(@Body taskRequest: TaskCreateRequest): TaskFetchResponse

    @DELETE(TASK_API_TASK_ID_ENDPOINT)
    suspend fun deleteTask(@Path("id") id: String): Unit

    @PATCH(TASK_API_TASK_ID_ENDPOINT)
    suspend fun updateTaskWithId(
        @Path("id") id: String,
        @Body task: TaskFetchResponse
    ): TaskFetchResponse
}