package com.justluxurylifestyle.get_things_done_droid

import java.io.InputStreamReader

class FileReader(path: String) {

    val content: String

    init {
        val reader = InputStreamReader(this.javaClass.classLoader!!.getResourceAsStream(path))
        content = reader.readText()
        reader.close()
    }

}
