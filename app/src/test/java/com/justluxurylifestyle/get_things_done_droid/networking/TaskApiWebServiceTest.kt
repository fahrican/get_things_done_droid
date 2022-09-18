package com.justluxurylifestyle.get_things_done_droid.networking

import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi.Companion.TASK_API_BASE_URL
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


internal class TaskApiWebServiceTest {

    @MockK
    lateinit var objectUnderTest: TaskApiWebService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun createOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Test
    fun `given two webservices then check if not equal`() {
        //given
        val mockWebService1 = TaskApiWebService.getTaskApiClient()
        val mockWebService2 = TaskApiWebService.getTaskApiClient()

        //then
        Assert.assertNotEquals(mockWebService1, mockWebService2)
    }

    @Test
    fun `given web service and retrofit client then check if not equal`() {
        //given
        val actualClient = Retrofit.Builder()
            .client(createOkHttpClient())
            .baseUrl(TASK_API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(TaskApi::class.java)

        //then
        Assert.assertNotEquals(objectUnderTest, actualClient)
    }
}