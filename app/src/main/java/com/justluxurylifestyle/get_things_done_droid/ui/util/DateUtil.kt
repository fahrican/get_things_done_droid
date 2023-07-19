package com.justluxurylifestyle.get_things_done_droid.ui.util

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtil {
    fun formatISO8601Date(dateString: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return parser.parse(dateString)?.let { formatter.format(it) } ?: ""
    }
}