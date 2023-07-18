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
    private val mockTaskApi: TaskApi = mockk()
    private lateinit var objectUnderTest: TaskRepository
    private lateinit var mockTaskRepository: TaskRepository


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
        mockTaskRepository = TaskRepositoryImpl(mockTaskApi)
    }

    @Test
    fun `when fetching all tasks then check for loading is null`() {
        val objectUnderTest = mockk<TaskRepository>()
        mockWebServer.enqueue(MockResponse().setBody(FileReader(TASKS_RESPONSE).content))

        var actualResult: List<TaskFetchResponse>?
        runBlocking {
            coEvery { objectUnderTest.getTasks(null) } returns ViewState.Loading
            actualResult = objectUnderTest.getTasks(null).extractData
        }
        assertEquals(null, actualResult)
    }

    @Test
    fun `when fetching all tasks then check for success response`() {
        // given
        val jsonArray = getJsonString<List<TaskFetchResponse>>(TASKS_RESPONSE)
        val gson = GsonBuilder().create()
        val listType = object : TypeToken<ArrayList<TaskFetchResponse?>?>() {}.type
        val expectedItems = gson.fromJson<List<TaskFetchResponse>>(jsonArray, listType)

        mockWebServer.enqueue(MockResponse().setBody(FileReader(TASKS_RESPONSE).content))

        var actualResult: List<TaskFetchResponse>?
        runBlocking {
            actualResult = objectUnderTest.getTasks(null).extractData
        }
        assertEquals(expectedItems, actualResult)
    }

    @Test
    fun `when fetching all tasks then check for error response`() {
        // given
        val expectedResponse = getJsonString<List<TaskFetchResponse>>(ERROR_RESPONSE)
        val gson = GsonBuilder().create()
        val listType = object : TypeToken<ArrayList<TaskFetchResponse?>?>() {}.type
        val expectedItems = gson.fromJson<List<TaskFetchResponse>>(expectedResponse, listType)

        mockWebServer.enqueue(MockResponse().setBody(FileReader(ERROR_RESPONSE).content))

        var actualResult: List<TaskFetchResponse>?
        runBlocking {
            actualResult = objectUnderTest.getTasks(null).extractData
        }
        assertEquals(expectedItems, actualResult)
    }

    @Test
    fun `when fetching all tasks then then check for http exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            val actualResult = objectUnderTest.getTasks(null)

            // Check the actualResult is ViewState.Error and contains correct status code
            assertTrue(actualResult is ViewState.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when fetching all tasks then then check for unknown exception`() {
        val exception = Exception("mock exception")

        coEvery { mockTaskRepository.getTasks(null) } throws exception


        runBlocking {
            val actualResult = mockTaskRepository.getTasks(null)

            assertTrue(actualResult is ViewState.Error)
            assertEquals(exception.message, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when fetch request for a specific task then check success response`() {
        val json = getJsonString<TaskFetchResponse>(TASK_BY_ID_RESPONSE)
        val gson = Gson()
        val expectedTask: TaskFetchResponse = gson.fromJson(json, TaskFetchResponse::class.java)

        mockWebServer.enqueue(MockResponse().setBody(FileReader(TASK_BY_ID_RESPONSE).content))

        runBlocking {
            val actualTask = objectUnderTest.getTaskById("825").extractData
            assertEquals(expectedTask, actualTask)
        }
    }

    @Test
    fun `when fetch request for a specific task then check for http exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))


        runBlocking {
            val actualResult = objectUnderTest.getTaskById("825")

            // Check the actualResult is ViewState.Error and contains correct status code
            assertTrue(actualResult is ViewState.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when fetch request for a specific task then check for unknown exception`() {
        val exception = Exception("mock exception")

        coEvery { mockTaskRepository.getTaskById(any()) } throws exception

        runBlocking {
            val actualResult = mockTaskRepository.getTaskById("825")

            assertTrue(actualResult is ViewState.Error)
            assertEquals(exception.message, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when post task request sent then check for success response`() {
        val json = getJsonString<TaskFetchResponse>(TASK_POST_REQUEST)
        val gson = Gson()
        val expectedTask: TaskFetchResponse = gson.fromJson(json, TaskFetchResponse::class.java)

        mockWebServer.enqueue(MockResponse().setBody(FileReader(TASK_POST_REQUEST).content))

        runBlocking {
            val createRequest = TaskCreateRequest(
                description = "test data",
                isReminderSet = true,
                isTaskOpen = true,
                priority = Priority.LOW
            )
            val actualTask = objectUnderTest.createTask(createRequest).extractData
            assertEquals(expectedTask, actualTask)
        }
    }

    @Test
    fun `when post task request sent then check for http exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            val createRequest = TaskCreateRequest(
                description = "test data",
                isReminderSet = true,
                isTaskOpen = true,
                priority = Priority.LOW
            )
            val actualResult = objectUnderTest.createTask(createRequest)

            assertTrue(actualResult is ViewState.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when post task request sent then check for unknown exception`() {
        val exception = Exception("mock exception")

        coEvery { mockTaskRepository.createTask(any()) } throws exception

        runBlocking {
            val createRequest = TaskCreateRequest(
                description = "test data",
                isReminderSet = true,
                isTaskOpen = true,
                priority = Priority.LOW
            )
            val actualResult = mockTaskRepository.createTask(createRequest)

            assertTrue(actualResult is ViewState.Error)
            assertEquals(exception.message, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when delete task request sent then check for successful removal`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(TaskRepositoryImpl.SUCCESS_NO_CONTENT))

        runBlocking {
            val actualMessage = objectUnderTest.canDeleteTask("15").extractData
            val isSuccessful: Boolean = actualMessage?.isSuccessful ?: false
            assertEquals(true, isSuccessful)
        }
    }

    @Test
    fun `when delete task request sent then check for http exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            val actualResult = objectUnderTest.canDeleteTask("2")

            assertTrue(actualResult is ViewState.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when delete task request sent then check for unknown exception`() {
        val exception = Exception("mock exception")

        coEvery { mockTaskRepository.canDeleteTask(any()) } throws exception

        runBlocking {
            val actualResult = mockTaskRepository.canDeleteTask("23")

            assertTrue(actualResult is ViewState.Error)
            assertEquals(exception.message, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when patch task request sent the check for success response`() {
        val expectedTask = getDataClass<TaskFetchResponse>(TASK_PUT_REQUEST)

        mockWebServer.enqueue(MockResponse().setBody(FileReader(TASK_PUT_REQUEST).content))

        runBlocking {
            val updateRequest = TaskUpdateRequest(description = "test test", null, null, null)
            val actualTask = objectUnderTest.updateTask("2", updateRequest).extractData
            assertEquals(expectedTask, actualTask)
        }
    }

    @Test
    fun `when patch task request sent the check for http exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            val task = TaskUpdateRequest(description = "test test", null, null, null)

            val actualResult = objectUnderTest.updateTask("2", task)

            assertTrue(actualResult is ViewState.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as ViewState.Error).exception.message)
        }
    }

    @Test
    fun `when patch task request sent  then check for unknown exception`() {
        val exception = Exception("mock exception")

        coEvery { mockTaskRepository.updateTask(any(), any()) } throws exception

        runBlocking {
            val updateRequest = TaskUpdateRequest(description = "test test", null, null, null)
            val actualResult = mockTaskRepository.updateTask("11", updateRequest)

            assertTrue(actualResult is ViewState.Error)
            assertEquals(exception.message, (actualResult as ViewState.Error).exception.message)
        }
    }
}