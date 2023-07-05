package com.justluxurylifestyle.get_things_done_droid.ui.view.epoxy

import com.airbnb.epoxy.EpoxyController
import com.justluxurylifestyle.get_things_done_droid.itemTask
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse

class TaskController(private val cardBgColor: Int) : EpoxyController() {

    private val myTasks = mutableListOf<TaskFetchResponse>()

    override fun buildModels() {
        myTasks.forEachIndexed { _, model ->
            itemTask {
                id(model.id)
                myTask(model)
                cardColor(this@TaskController.cardBgColor)
            }
        }
    }

    fun setTasks(taskItems: List<TaskFetchResponse>) {
        this.myTasks.clear()
        this.myTasks.addAll(taskItems)
        requestModelBuild()
    }

    fun getNumberOfMyTasks(): Int = myTasks.size

    fun deleteItem(index: Int) {
        myTasks.removeAt(index)
        requestModelBuild()
    }

    fun addItem(index: Int, myTask: TaskFetchResponse) {
        myTasks.add(index, myTask)
        requestModelBuild()
    }

    fun getTaskById(index: Int): TaskFetchResponse = myTasks[index]
}