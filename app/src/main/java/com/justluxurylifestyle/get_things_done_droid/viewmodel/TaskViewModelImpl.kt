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

    private val _isDeleteSuccessful = MutableLiveData<Boolean>()
    val isDeleteSuccessful: LiveData<Boolean>
        get() = _isDeleteSuccessful

    override fun fetchTasks(status: String?) {
        launchViewStateJob(_tasks) { repository.getTasks(status) }
    }

    override fun fetchTaskById(id: String) {
        launchViewStateJob(_task) { repository.getTaskById(id) }
    }

    override fun createTask(createRequest: TaskCreateRequest) {
        launchViewStateJob(_task) { repository.createTask(createRequest) }
    }

    override fun deleteTask(id: String) {
        viewModelScope.launch {
            when (repository.canDeleteTask(id)) {
                is ViewState.Success -> _isDeleteSuccessful.postValue(true)
                is ViewState.Error -> _isDeleteSuccessful.postValue(false)
                else -> Timber.d("ViewModel delete task process")
            }
        }
    }

    override fun updateTask(
        id: String,
        updateRequest: TaskUpdateRequest
    ) {
        launchViewStateJob(_task) { repository.updateTask(id, updateRequest) }
    }

    private fun <T : Any> launchViewStateJob(
        liveData: MutableLiveData<ViewState<T>>,
        action: suspend () -> ViewState<T>
    ) {
        liveData.postValue(ViewState.Loading)
        viewModelScope.launch {
            val response = action()
            liveData.postValue(response)
            when (response) {
                is ViewState.Success -> Timber.d("success block: $response")
                is ViewState.Error -> Timber.d("error block: $response")
                else -> Timber.d("else block: $response")
            }
        }
    }
}
