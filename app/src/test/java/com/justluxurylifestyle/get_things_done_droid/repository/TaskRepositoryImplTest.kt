package com.justluxurylifestyle.get_things_done_droid.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.justluxurylifestyle.get_things_done_droid.BaseRepoTest
import com.justluxurylifestyle.get_things_done_droid.FileReader
import com.justluxurylifestyle.get_things_done_droid.core.BaseRepository.Companion.SOMETHING_WRONG
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.Priority
import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.model.TaskUpdateRequest
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
internal class TaskRepositoryImplTest : BaseRepoTest() {

    companion object {
        private const val TASKS_RESPONSE = "tasks_response.json"
        private const val TASK_BY_ID_RESPONSE = "get_task_by_id_response.json"
        private const val ERROR_RESPONSE = "error_task_response.json"
        private const val TASK_POST_REQUEST = "post_request_task.json"
        private const val TASK_PUT_REQUEST = "put_request_task.json"
        private const val INTERNAL_SERVER_ERROR: Int = 500
    }

    private lateinit var taskApi: TaskApi
    private lateinit var objectUnderTest: TaskRepository


    @Before
    override fun setUp() {
        MockKAnnotations.init(this)

        taskApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create()) //important
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(TaskApi::class.java)

        objectUnderTest = TaskRepositoryImpl(taskApi)
    }

    @Test
    fun `given repo when fetching all tasks then check loading is null`() {
        val objectUnderTest = mockk<TaskRepository>()
        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(SUCCESS_RESPONSE).content))
        }

        var actualResult: List<TaskFetchResponse>?
        runBlocking {
            coEvery { objectUnderTest.getTasks(null) } returns ViewState.Loading
            val actualResponse: ViewState<List<TaskFetchResponse>> =
                objectUnderTest.getTasks(null)
            actualResult = actualResponse.extractData
        }
        assertEquals(null, actualResult)
    }

    @Test
    fun `given api when fetching all tasks then check success response`() {
        // given
        val jsonArray = getJsonString<List<TaskFetchResponse>>(SUCCESS_RESPONSE)
        val gson = GsonBuilder().create()
        val listType = object : TypeToken<ArrayList<TaskFetchResponse?>?>() {}.type
        val expectedItems = gson.fromJson<List<TaskFetchResponse>>(jsonArray, listType)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(SUCCESS_RESPONSE).content))
        }

        var actualResult: List<TaskFetchResponse>?
        runBlocking {
            val actualResponse: ViewState<List<TaskFetchResponse>> =
                objectUnderTest.getTasks(null)
            actualResult = actualResponse.extractData
        }
        assertEquals(expectedItems, actualResult)
    }

    @Test
    fun `given api when fetching all tasks then get error response`() {
        // given
        val expectedResponse = getJsonString<List<TaskFetchResponse>>(ERROR_RESPONSE)
        val gson = GsonBuilder().create()
        val listType = object : TypeToken<ArrayList<TaskFetchResponse?>?>() {}.type
        val expectedItems = gson.fromJson<List<TaskFetchResponse>>(expectedResponse, listType)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(ERROR_RESPONSE).content))
        }

        var actualResult: List<TaskFetchResponse>?
        runBlocking {
            val actualResponse: ViewState<List<TaskFetchResponse>> =
                objectUnderTest.getTasks(null)
            actualResult = actualResponse.extractData
        }
        assertEquals(expectedItems, actualResult)
    }

    @Test
    fun `given api when fetching all tasks then return exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            val actualResult = objectUnderTest.getTasks(null)

            // Check the actualResult is ViewState.Error and contains correct status code
            assertTrue(actualResult is ViewState.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when post task request send check for successful creation`() {
        val json = getJsonString<TaskFetchResponse>(TASK_POST_REQUEST)
        val gson = Gson()
        val expectedTask: TaskFetchResponse = gson.fromJson(json, TaskFetchResponse::class.java)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(TASK_POST_REQUEST).content))
        }

        runBlocking {
            val taskReq = TaskCreateRequest("test data", true, true, Priority.LOW)
            val actualTask = objectUnderTest.createTask(taskReq).extractData
            assertEquals(expectedTask, actualTask)
        }
    }

    @Test
    fun `given post task request then return exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))


        runBlocking {
            val taskReq = TaskCreateRequest(
                description = "test data",
                isReminderSet = true,
                isTaskOpen = true,
                priority = Priority.LOW
            )
            val actualResult = objectUnderTest.createTask(taskReq)

            // Check the actualResult is ViewState.Error and contains correct status code
            assertTrue(actualResult is ViewState.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when delete task request check for successful removal`() {
        mockWebServer.apply {
            enqueue(MockResponse().setResponseCode(TaskRepositoryImpl.SUCCESS_NO_CONTENT))
        }

        runBlocking {
            val actualMessage = objectUnderTest.canDeleteTask("15").extractData
            val isSuccessful: Boolean = actualMessage?.isSuccessful ?: false
            assertEquals(true, isSuccessful)
        }
    }

    @Test
    fun `given delete task request then return exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            // Perform the API call
            val actualResult = objectUnderTest.canDeleteTask("2")

            // Check the actualResult is ViewState.Error and contains correct status code
            assertTrue(actualResult is ViewState.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when patch task request check for successful update`() {
        val expectedTask = getDataClass<TaskFetchResponse>(TASK_PUT_REQUEST)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(TASK_PUT_REQUEST).content))
        }

        runBlocking {
            val task = TaskUpdateRequest(description = "test test", null, null, null)
            val actualTask = objectUnderTest.updateTask("2", task).extractData
            assertEquals(expectedTask, actualTask)
        }
    }

    /*    @Test
        fun `given put task request then return exception`() {
            mockWebServer.apply {
                enqueue(MockResponse().setResponseCode(GENERAL_ERROR_CODE))
            }

            every { httpException.message } returns SOMETHING_WRONG

            runBlocking {
                val task = TaskUpdateRequest(description = "test test", null, null, null)
                val apiResponse = objectUnderTest.updateTask("1", task)
                Assert.assertNotNull(apiResponse)
                val expectedValue = ViewState.Error(httpException)

                assertEquals(
                    expectedValue.exception.message,
                    (apiResponse as ViewState.Error).exception.message
                )
            }
        }*/

    @Test
    fun `when patch task request, should handle HttpException`() {
        // Enqueue an error response
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            val task = TaskUpdateRequest(description = "test test", null, null, null)

            // Perform the API call
            val actualResult = objectUnderTest.updateTask("2", task)

            // Check the actualResult is ViewState.Error and contains correct status code
            assertTrue(actualResult is ViewState.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as ViewState.Error).exception.message)
        }
    }
}