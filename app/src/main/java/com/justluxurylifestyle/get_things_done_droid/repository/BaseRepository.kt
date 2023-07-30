package com.justluxurylifestyle.get_things_done_droid.repository

import com.justluxurylifestyle.get_things_done_droid.core.StateOfView


abstract class BaseRepository {

    companion object {
        private const val BAD_REQUEST = "Bad Request"
        private const val UNAUTHORIZED = "Unauthorized"
        private const val NOT_FOUND = "Not found"
        private const val SOMETHING_WRONG = "Something went wrong"


        fun <T : Any> handleSuccess(data: T): StateOfView<T> {
            return StateOfView.Success(data)
        }

        fun <T : Any> handleException(code: Int): StateOfView<T> {
            val exception = getErrorMessage(code)
            return StateOfView.Error(Exception(exception))
        }

        private fun getErrorMessage(httpCode: Int): String {
            return when (httpCode) {
                400 -> BAD_REQUEST
                401 -> UNAUTHORIZED
                404 -> NOT_FOUND
                else -> SOMETHING_WRONG
            }
        }
    }
}