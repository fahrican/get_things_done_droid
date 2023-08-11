package com.justluxurylifestyle.get_things_done_droid.ui.view.epoxy

import com.airbnb.epoxy.EpoxyController
import com.justluxurylifestyle.get_things_done_droid.itemTask
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskFetchResponse

class TaskController(private val cardBgColor: Int) : EpoxyController() {

    private val tasks = mutableListOf<TaskFetchResponse>()

    override fun buildModels() {
        tasks.forEachIndexed { _, model ->
            itemTask {
                id(model.id)
                myTask(model)
                cardColor(this@TaskController.cardBgColor)
            }
        }
    }

    fun setTasks(taskItems: List<TaskFetchResponse>) {
        this.tasks.clear()
        this.tasks.addAll(taskItems)
        requestModelBuild()
    }

    fun getNumberOfMyTasks(): Int = tasks.size

    fun getTaskById(index: Int): TaskFetchResponse = tasks[index]
}