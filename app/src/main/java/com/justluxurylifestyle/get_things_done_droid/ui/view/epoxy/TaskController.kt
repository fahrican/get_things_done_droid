package com.justluxurylifestyle.get_things_done_droid.ui.view.epoxy

import com.airbnb.epoxy.EpoxyController
import com.justluxurylifestyle.get_things_done_droid.itemTask
import com.justluxurylifestyle.get_things_done_droid.model.MyTask

class TaskController : EpoxyController() {

    private val myTasks = mutableListOf<MyTask>()

    override fun buildModels() {
        myTasks.forEachIndexed { _, model ->
            itemTask {
                id(model.task?.id)
                myTask(model)
            }
        }
    }

    fun setTasks(taskItems: List<MyTask>) {
        this.myTasks.clear()
        this.myTasks.addAll(taskItems)
        requestModelBuild()
    }

    fun getNumberOfMyTasks(): Int = myTasks.size
}