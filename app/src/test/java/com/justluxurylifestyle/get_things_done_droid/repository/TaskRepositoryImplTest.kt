package com.justluxurylifestyle.get_things_done_droid.repository

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.justluxurylifestyle.get_things_done_droid.BaseRepoTest
import com.justluxurylifestyle.get_things_done_droid.FileReader
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi.Companion.ALL_TASKS
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
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
        private val SUCCESS_RESPONSE = "successful_task_response.json"
        private val ERROR_RESPONSE = "error_task_response.json"
    }

    private lateinit var objectUnderTest: TaskRepositoryImpl

    private lateinit var taskApi: TaskApi

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
    fun `given api when fetching bitcoin articles then check success response`() {
        // given
        val expectedResponse = getJsonString<List<TaskResponseItem>>(SUCCESS_RESPONSE)
        val gson = GsonBuilder().create()
        val listType = object : TypeToken<ArrayList<TaskResponseItem?>?>() {}.type

        val newList = gson.fromJson<List<TaskResponseItem>>(expectedResponse, listType)


        mockWebServer.apply {
            enqueue(MockResponse().setBody(FileReader(SUCCESS_RESPONSE).content))
        }

        var actualResult: List<TaskResponseItem>?
        runBlocking {
            val actualResponse: ViewState<List<TaskResponseItem>> =
                objectUnderTest.getTasks(ALL_TASKS)
            actualResult = actualResponse.extractData
        }
        assertEquals(newList, actualResult)
    }
}