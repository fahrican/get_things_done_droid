package com.justluxurylifestyle.get_things_done_droid.ui.open_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class OpenTaskViewModel @Inject constructor(
    private val repository: OpenTaskRepository
) : ViewModel() {

    private val _tasks = MutableLiveData<ViewState<List<TaskResponseItem>>>()
    val tasks: LiveData<ViewState<List<TaskResponseItem>>>
        get() = _tasks

    fun fetchOpenTasks(endpoint: String) {
        _tasks.postValue(ViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getTasks(endpoint)
            response.let { data ->
                when (data) {
                    is ViewState.Success -> {
                        _tasks.postValue(data)
                    }
                    is ViewState.Error -> {
                        _tasks.postValue(data)
                        Timber.d("error block: $response")
                    }
                    else -> {
                        _tasks.postValue(data)
                        Timber.d("else block: $response")
                    }
                }
            }
        }
    }
}