package com.justluxurylifestyle.get_things_done_droid.model

data class ErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val path: String? = null,
    val status: Int? = null,
    val timestamp: String? = null
)