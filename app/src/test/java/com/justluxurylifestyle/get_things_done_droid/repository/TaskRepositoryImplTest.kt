package com.justluxurylifestyle.get_things_done_droid.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.justluxurylifestyle.get_things_done_droid.BaseRepoTest
import com.justluxurylifestyle.get_things_done_droid.FileReader
import com.justluxurylifestyle.get_things_done_droid.core.StateOfView
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.Priority
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskUpdateRequest
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.http_client.TaskApi
import io.mockk.MockKException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


internal class TaskRepositoryImplTest : BaseRepoTest() {

    companion object {
        private const val TASKS_RESPONSE = "get_tasks_response.json"
        private const val TASK_BY_ID_RESPONSE = "get_task_by_id_response.json"
        private const val TASK_POST_REQUEST = "post_request_task.json"
        private const val TASK_PATCH_REQUEST = "patch_request_task.json"
        private const val INTERNAL_SERVER_ERROR: Int = 500
        private const val SUCCESS_NO_CONTENT: Int = 204
        private const val SOMETHING_WRONG = "Something went wrong"
    }

    private val gson = GsonBuilder().create()

    private val exception = Exception("triggered exception")

    private val createRequest = TaskCreateRequest(
        description = "test data",
        isReminderSet = true,
        isTaskOpen = true,
        priority = Priority.LOW,
        timeInterval = "2 weeks"
    )

    private val updateRequest = TaskUpdateRequest(
        description = "test test",
        isReminderSet = null,
        isTaskOpen = null,
        priority = null,
        startedOn = null,
        finishedOn = null,
        timeInterval = "2 weeks",
        timeTaken = 3
    )

    private lateinit var taskApi: TaskApi
    private lateinit var objectUnderTest: TaskRepository

    private val mockTaskApi = mockk<TaskApi>()
    private val mockTaskRepository = TaskRepositoryImpl(mockTaskApi)


    @Before
    override fun setUp() {
        taskApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create()) //important
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(TaskApi::class.java)

        objectUnderTest = TaskRepositoryImpl(taskApi)
    }

    @Test
    fun `when fetching all tasks then check for success response`() {
        // given
        val jsonArray = getJsonString<List<TaskFetchResponse>>(TASKS_RESPONSE)
        val listType = object : TypeToken<ArrayList<TaskFetchResponse?>?>() {}.type
        val expectedItems = gson.fromJson<List<TaskFetchResponse>>(jsonArray, listType)

        mockWebServer.enqueue(MockResponse().setBody(FileReader(TASKS_RESPONSE).content))

        runBlocking {
            // when
            val actualResult = objectUnderTest.getTasks(null).extractData
            // then
            assertEquals(expectedItems, actualResult)
        }
    }

    @Test
    fun `when fetching all tasks then then check for http exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            val actualResult = objectUnderTest.getTasks(null)

            assertTrue(actualResult is StateOfView.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as StateOfView.Error).exception.message)
        }
    }

    @Test
    fun `when fetching all tasks then then check for unknown exception`() {
        coEvery { mockTaskRepository.getTasks(null) } throws exception


        runBlocking {
            val actualResult = mockTaskRepository.getTasks(null)

            assertTrue(actualResult is StateOfView.Error)
            assertEquals(exception.message, (actualResult as StateOfView.Error).exception.message)
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

            assertTrue(actualResult is StateOfView.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as StateOfView.Error).exception.message)
        }
    }

    @Test
    fun `when fetch request for a specific task then check for unknown exception`() {
        coEvery { mockTaskRepository.getTaskById(any()) } throws exception

        runBlocking {
            val actualResult = mockTaskRepository.getTaskById("825")

            assertTrue(actualResult is StateOfView.Error)
            assertEquals(exception.message, (actualResult as StateOfView.Error).exception.message)
        }
    }

    @Test
    fun `when post task request sent then check for success response`() {
        val json = getJsonString<TaskFetchResponse>(TASK_POST_REQUEST)
        val gson = Gson()
        val expectedTask: TaskFetchResponse = gson.fromJson(json, TaskFetchResponse::class.java)

        mockWebServer.enqueue(MockResponse().setBody(FileReader(TASK_POST_REQUEST).content))

        runBlocking {
            val actualTask = objectUnderTest.createTask(createRequest).extractData
            assertEquals(expectedTask, actualTask)
        }
    }

    @Test
    fun `when post task request sent then check for http exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            val actualResult = objectUnderTest.createTask(createRequest)

            assertTrue(actualResult is StateOfView.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as StateOfView.Error).exception.message)
        }
    }

    @Test
    fun `when post task request sent then check for unknown exception`() {
        coEvery { mockTaskRepository.createTask(any()) } throws exception

        runBlocking {
            val actualResult = mockTaskRepository.createTask(createRequest)

            assertTrue(actualResult is StateOfView.Error)
            assertEquals(exception.message, (actualResult as StateOfView.Error).exception.message)
        }
    }

    @Test
    fun `when delete task request sent then check for successful removal`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(SUCCESS_NO_CONTENT))

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

            assertTrue(actualResult is StateOfView.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as StateOfView.Error).exception.message)
        }
    }

    @Test
    fun `when delete task request sent then check for unknown exception`() {
        val mockException = MockKException("mocked exception")
        coEvery { mockTaskRepository.canDeleteTask(any()) } throws mockException

        runBlocking {
            val actualResult = mockTaskRepository.canDeleteTask("23")

            assertTrue(actualResult is StateOfView.Error)
            assertEquals(mockException::class, (actualResult as StateOfView.Error).exception::class)
        }
    }

    @Test
    fun `when patch task request sent then check for success response`() {
        val expectedTask = getDataClass<TaskFetchResponse>(TASK_PATCH_REQUEST)

        mockWebServer.enqueue(MockResponse().setBody(FileReader(TASK_PATCH_REQUEST).content))

        runBlocking {
            val actualTask = objectUnderTest.updateTask("605", updateRequest).extractData
            assertEquals(expectedTask, actualTask)
        }
    }

    @Test
    fun `when patch task request sent then check for http exception`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            val actualResult = objectUnderTest.updateTask("2", updateRequest)

            assertTrue(actualResult is StateOfView.Error)
            assertEquals(SOMETHING_WRONG, (actualResult as StateOfView.Error).exception.message)
        }
    }

    @Test
    fun `when patch task request sent then check for unknown exception`() {
        coEvery { mockTaskRepository.updateTask(any(), any()) } throws exception

        runBlocking {
            val actualResult = mockTaskRepository.updateTask("11", updateRequest)

            assertTrue(actualResult is StateOfView.Error)
            assertEquals(exception.message, (actualResult as StateOfView.Error).exception.message)
        }
    }
}