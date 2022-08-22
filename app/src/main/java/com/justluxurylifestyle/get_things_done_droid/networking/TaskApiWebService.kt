package com.justluxurylifestyle.get_things_done_droid.networking

import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi.Companion.TASK_API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object TaskApiWebService {
    private fun createOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    fun getTaskApiClient(): TaskApi {
        val okHttpClient = createOkHttpClient()
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(TASK_API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(TaskApi::class.java)
    }
}