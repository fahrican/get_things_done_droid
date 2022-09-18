package com.justluxurylifestyle.get_things_done_droid

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.mockk.MockKAnnotations
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
abstract class BaseRepoTest {

    val mockWebServer = MockWebServer()

    @Before
    open fun setUp() {
        mockWebServer.start(8080)
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    inline fun <reified T : Any> getDataClass(jsonFile: String): T? {
        val reader = FileReader(jsonFile)
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
        return jsonAdapter.fromJson(reader.content)
    }
}