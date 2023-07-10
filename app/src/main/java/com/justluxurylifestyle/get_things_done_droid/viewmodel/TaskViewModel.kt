package com.justluxurylifestyle.get_things_done_droid.viewmodel

import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.model.TaskUpdateRequest

interface TaskViewModel {

    fun fetchTasks(endpoint: String?)

    fun createTask(createRequest: TaskCreateRequest)

    fun deleteTask(id: String)

    fun updateTask(id: String, updateRequest: TaskUpdateRequest)
}