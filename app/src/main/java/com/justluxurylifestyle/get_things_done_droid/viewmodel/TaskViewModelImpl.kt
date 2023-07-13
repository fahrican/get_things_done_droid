package com.justluxurylifestyle.get_things_done_droid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.model.TaskUpdateRequest
import com.justluxurylifestyle.get_things_done_droid.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class TaskViewModelImpl @Inject constructor(
    private val repository: TaskRepository
) : ViewModel(), TaskViewModel {

    private val _tasks = MutableLiveData<ViewState<List<TaskFetchResponse>>>()
    val tasks: LiveData<ViewState<List<TaskFetchResponse>>>
        get() = _tasks

    private val _task = MutableLiveData<ViewState<TaskFetchResponse>>()
    val task: LiveData<ViewState<TaskFetchResponse>>
        get() = _task

    private val _deleteTaskText = MutableLiveData<ViewState<Response<Unit>>>()
    val deleteTaskText: LiveData<ViewState<Response<Unit>>>
        get() = _deleteTaskText

    private fun <T : Any> handleResponse(
        liveData: MutableLiveData<ViewState<T>>,
        response: ViewState<T>
    ) {
        liveData.postValue(response)
        when (response) {
            is ViewState.Success -> Timber.d("success block: $response")
            is ViewState.Error -> Timber.d("error block: $response")
            else -> Timber.d("else block: $response")
        }
    }

    override fun fetchTasks(status: String?) {
        _tasks.postValue(ViewState.Loading)
        viewModelScope.launch {
            handleResponse(_tasks, repository.getTasks(status))
        }
    }

    override fun fetchTaskById(id: String) {
        _task.postValue(ViewState.Loading)
        viewModelScope.launch {
            handleResponse(_task, repository.getTaskById(id))
        }
    }

    override fun createTask(createRequest: TaskCreateRequest) {
        _task.postValue(ViewState.Loading)
        viewModelScope.launch {
            handleResponse(_task, repository.createTask(createRequest))
        }
    }

    override fun deleteTask(id: String) {
        _deleteTaskText.postValue(ViewState.Loading)
        viewModelScope.launch {
            handleResponse(_deleteTaskText, repository.deleteTask(id))
        }
    }

    override fun updateTask(
        id: String,
        updateRequest: TaskUpdateRequest
    ) {
        _task.postValue(ViewState.Loading)
        viewModelScope.launch {
            handleResponse(_task, repository.updateTask(id, updateRequest))
        }
    }
}
