package com.justluxurylifestyle.get_things_done_droid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableLiveData<ViewState<List<TaskFetchResponse>>>()
    val tasks: LiveData<ViewState<List<TaskFetchResponse>>>
        get() = _tasks

    private val _task = MutableLiveData<ViewState<TaskFetchResponse>>()
    val task: LiveData<ViewState<TaskFetchResponse>>
        get() = _task

    private val _deleteTaskText = MutableLiveData<ViewState<Unit>>()
    val deleteTaskText: LiveData<ViewState<Unit>>
        get() = _deleteTaskText

    fun fetchTasks(endpoint: String?) {
        _tasks.postValue(ViewState.Loading)
        viewModelScope.launch {
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

    fun createTask(taskRequest: TaskCreateRequest) {
        _task.postValue(ViewState.Loading)
        viewModelScope.launch {
            val response = repository.createTask(taskRequest)
            response.let { data ->
                when (data) {
                    is ViewState.Success -> {
                        _task.postValue(data)
                        Timber.d("success block: $response")
                    }
                    is ViewState.Error -> {
                        _task.postValue(data)
                        Timber.d("error block: $response")
                    }
                    else -> {
                        _task.postValue(data)
                        Timber.d("else block: $response")
                    }
                }
            }
        }
    }

    fun deleteTask(id: String) {
        _deleteTaskText.postValue(ViewState.Loading)
        viewModelScope.launch {
            val response = repository.deleteTask(id)
            response.let { data ->
                when (data) {
                    is ViewState.Success -> {
                        _deleteTaskText.postValue(data)
                        Timber.d("deleteTask success block: $response")
                    }
                    is ViewState.Error -> {
                        _deleteTaskText.postValue(data)
                        Timber.d("deleteTask error block: $response")
                    }
                    else -> {
                        _deleteTaskText.postValue(data)
                        Timber.d("deleteTask else block: $response")
                    }
                }
            }
        }
    }

    fun updateTask(task: TaskFetchResponse) {
        _task.postValue(ViewState.Loading)
        viewModelScope.launch {
            val response = repository.updateTask(task)
            response.let { data ->
                when (data) {
                    is ViewState.Success -> {
                        _task.postValue(data)
                        Timber.d("success block: $response")
                    }
                    is ViewState.Error -> {
                        _task.postValue(data)
                        Timber.d("error block: $response")
                    }
                    else -> {
                        _task.postValue(data)
                        Timber.d("else block: $response")
                    }
                }
            }
        }
    }
}