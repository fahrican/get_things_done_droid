package com.justluxurylifestyle.get_things_done_droid.core

sealed class ViewState<out T : Any> {

    data class Success<out T : Any>(val data: T) : ViewState<T>()
    data class Error(val exception: Exception) : ViewState<Nothing>()
    object Loading : ViewState<Nothing>()

    val extractData: T?
        get() = when (this) {
            is Success -> data
            is Error -> null
            Loading -> null
        }

}