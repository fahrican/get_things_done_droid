package com.justluxurylifestyle.get_things_done_droid.viewmodel

import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskUpdateRequest


interface TaskViewModel {

    fun fetchTasks(status: String?)

    fun fetchTaskById(id: String)

    fun createTask(createRequest: TaskCreateRequest)

    fun deleteTask(id: String)

    fun updateTask(id: String, updateRequest: TaskUpdateRequest)
}