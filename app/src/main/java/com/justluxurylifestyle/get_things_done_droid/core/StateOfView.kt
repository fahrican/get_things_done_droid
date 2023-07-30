package com.justluxurylifestyle.get_things_done_droid.core

sealed class StateOfView<out T : Any> {

    data class Success<out T : Any>(val data: T) : StateOfView<T>()
    data class Error(val exception: Exception) : StateOfView<Nothing>()
    object Loading : StateOfView<Nothing>()

    val extractData: T?
        get() = when (this) {
            is Success -> data
            is Error -> null
            Loading -> null
        }

}