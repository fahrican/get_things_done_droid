package com.justluxurylifestyle.get_things_done_droid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justluxurylifestyle.get_things_done_droid.core.StateOfView
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

    private val _tasks = MutableLiveData<StateOfView<List<TaskFetchResponse>>>()
    val tasks: LiveData<StateOfView<List<TaskFetchResponse>>>
        get() = _tasks

    private val _task = MutableLiveData<StateOfView<TaskFetchResponse>>()
    val task: LiveData<StateOfView<TaskFetchResponse>>
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
                is StateOfView.Success -> _isDeleteSuccessful.postValue(true)
                is StateOfView.Error -> _isDeleteSuccessful.postValue(false)
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
        liveData: MutableLiveData<StateOfView<T>>,
        action: suspend () -> StateOfView<T>
    ) {
        liveData.postValue(StateOfView.Loading)
        viewModelScope.launch {
            val response = action()
            liveData.postValue(response)
            when (response) {
                is StateOfView.Success -> Timber.d("success block: $response")
                is StateOfView.Error -> Timber.d("error block: $response")
                else -> Timber.d("else block: $response")
            }
        }
    }
}
