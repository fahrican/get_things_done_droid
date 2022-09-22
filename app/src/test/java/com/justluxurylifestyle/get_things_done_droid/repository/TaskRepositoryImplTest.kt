package com.justluxurylifestyle.get_things_done_droid.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.justluxurylifestyle.get_things_done_droid.BaseRepoTest
import com.justluxurylifestyle.get_things_done_droid.FileReader
import com.justluxurylifestyle.get_things_done_droid.core.BaseRepository.Companion.GENERAL_ERROR_CODE
import com.justluxurylifestyle.get_things_done_droid.core.BaseRepository.Companion.SOMETHING_WRONG
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi.Companion.ALL_TASKS
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
internal class TaskRepositoryImplTest : BaseRepoTest() {

    companion object {
        private const val SUCCESS_RESPONSE = "successful_task_response.json"
        private const val ERROR_RESPONSE = "error_task_response.json"
        private const val TASK_POST_REQUEST = "post_request_task.json"
        private const val TASK_DELETE_REQUEST = "delete_request_task.json"
        private const val TASK_PUT_REQUEST = "put_request_task.json"
    }

    @RelaxedMockK
    private lateinit var httpException: HttpException

    private lateinit var objectUnderTest: TaskRepositoryImpl

    private lateinit var taskApi: TaskApi

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

        var actualResult: List<TaskResponseItem>?
        runBlocking {
            coEvery { objectUnderTest.getTasks(ALL_TASKS) } returns ViewState.Loading
            val actualResponse: ViewState<List<TaskResponseItem>> =
                objectUnderTest.getTasks(ALL_TASKS)
            actualResult = actualResponse.extractData
        }
        assertEquals(null, actualResult)
    }

    @Test
    fun `given api when fetching all tasks then check success response`() {
        // given
        val jsonArray = getJsonString<List<TaskResponseItem>>(SUCCESS_RESPONSE)
        val gson = GsonBuilder().create()
        val listType = object : TypeToken<ArrayList<TaskResponseItem?>?>() {}.type
        val expectedItems = gson.fromJson<List<TaskResponseItem>>(jsonArray, listType)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(SUCCESS_RESPONSE).content))
        }

        var actualResult: List<TaskResponseItem>?
        runBlocking {
            val actualResponse: ViewState<List<TaskResponseItem>> =
                objectUnderTest.getTasks(ALL_TASKS)
            actualResult = actualResponse.extractData
        }
        assertEquals(expectedItems, actualResult)
    }

    @Test
    fun `given api when fetching all tasks then get error response`() {
        // given
        val expectedResponse = getJsonString<List<TaskResponseItem>>(ERROR_RESPONSE)
        val gson = GsonBuilder().create()
        val listType = object : TypeToken<ArrayList<TaskResponseItem?>?>() {}.type
        val expectedItems = gson.fromJson<List<TaskResponseItem>>(expectedResponse, listType)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(ERROR_RESPONSE).content))
        }

        var actualResult: List<TaskResponseItem>?
        runBlocking {
            val actualResponse: ViewState<List<TaskResponseItem>> =
                objectUnderTest.getTasks(ALL_TASKS)
            actualResult = actualResponse.extractData
        }
        assertEquals(expectedItems, actualResult)
    }

    @Test
    fun `given api when fetching all tasks then return exception`() {
        mockWebServer.apply {
            enqueue(MockResponse().setResponseCode(GENERAL_ERROR_CODE))
        }

        every { httpException.message } returns SOMETHING_WRONG

        runBlocking {
            val apiResponse = objectUnderTest.getTasks(ALL_TASKS)
            Assert.assertNotNull(apiResponse)
            val expectedValue = ViewState.Error(httpException)

            assertEquals(
                expectedValue.exception.message,
                (apiResponse as ViewState.Error).exception.message
            )
        }
    }

    @Test
    fun `when post task request send check for successful creation`() {
        val json = getJsonString<TaskResponseItem>(TASK_POST_REQUEST)
        val gson = Gson()
        val expectedTask: TaskResponseItem = gson.fromJson(json, TaskResponseItem::class.java)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(TASK_POST_REQUEST).content))
        }

        runBlocking {
            val taskReq = TaskResponseItem()
            val actualTask = objectUnderTest.createTask(taskReq).extractData
            assertEquals(expectedTask, actualTask)
        }
    }

    @Test
    fun `given post task request then return exception`() {
        mockWebServer.apply {
            enqueue(MockResponse().setResponseCode(GENERAL_ERROR_CODE))
        }

        every { httpException.message } returns SOMETHING_WRONG

        runBlocking {
            val taskReq = TaskResponseItem()
            val apiResponse = objectUnderTest.createTask(taskReq)
            Assert.assertNotNull(apiResponse)
            val expectedValue = ViewState.Error(httpException)

            assertEquals(
                expectedValue.exception.message,
                (apiResponse as ViewState.Error).exception.message
            )
        }
    }

    @Test
    fun `when delete task request check for successful removal`() {
        val expectedMessage = "Task with id: 15 was successful deleted"

        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(TASK_DELETE_REQUEST).content))
        }

        runBlocking {
            val actualMessage = objectUnderTest.deleteTask("15").extractData
            assertEquals(expectedMessage, actualMessage)
        }
    }

    @Test
    fun `given delete task request then return exception`() {
        mockWebServer.apply {
            enqueue(MockResponse().setResponseCode(GENERAL_ERROR_CODE))
        }

        every { httpException.message } returns SOMETHING_WRONG

        runBlocking {
            val apiResponse = objectUnderTest.deleteTask("15")
            Assert.assertNotNull(apiResponse)
            val expectedValue = ViewState.Error(httpException)

            assertEquals(
                expectedValue.exception.message,
                (apiResponse as ViewState.Error).exception.message
            )
        }
    }

    @Test
    fun `when put task request check for successful update`() {
        val expectedTask = getDataClass<TaskResponseItem>(TASK_PUT_REQUEST)

        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(TASK_PUT_REQUEST).content))
        }

        runBlocking {
            val task = TaskResponseItem(description = "test test", startedOn = "2020.09.09")
            val actualTask = objectUnderTest.updateTask(task).extractData
            assertEquals(expectedTask, actualTask)
        }
    }

    @Test
    fun `given put task request then return exception`() {
        mockWebServer.apply {
            enqueue(MockResponse().setResponseCode(GENERAL_ERROR_CODE))
        }

        every { httpException.message } returns SOMETHING_WRONG

        runBlocking {
            val task = TaskResponseItem(description = "test test", startedOn = "2020.09.09")
            val apiResponse = objectUnderTest.updateTask(task)
            Assert.assertNotNull(apiResponse)
            val expectedValue = ViewState.Error(httpException)

            assertEquals(
                expectedValue.exception.message,
                (apiResponse as ViewState.Error).exception.message
            )
        }
    }
}