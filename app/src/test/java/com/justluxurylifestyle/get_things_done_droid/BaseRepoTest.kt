package com.justluxurylifestyle.get_things_done_droid

import com.google.gson.GsonBuilder
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
        val gson = GsonBuilder().create()
        return gson.fromJson(reader.content, T::class.java)
    }

    inline fun <reified T : Any> getJsonString(jsonFile: String): String {
        val reader = FileReader(jsonFile)
        return reader.content
    }
}