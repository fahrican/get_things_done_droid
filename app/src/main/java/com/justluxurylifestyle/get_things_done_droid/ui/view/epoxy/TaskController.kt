package com.justluxurylifestyle.get_things_done_droid.ui.view.epoxy

import com.airbnb.epoxy.EpoxyController
import com.justluxurylifestyle.get_things_done_droid.itemTask
import com.justluxurylifestyle.get_things_done_droid.model.MyTask

class TaskController(private val cardBgColor: Int) : EpoxyController() {

    private val myTasks = mutableListOf<MyTask>()

    override fun buildModels() {
        myTasks.forEachIndexed { _, model ->
            itemTask {
                id(model.task?.id)
                myTask(model)
                cardColor(this@TaskController.cardBgColor)
            }
        }
    }

    fun setTasks(taskItems: List<MyTask>) {
        this.myTasks.clear()
        this.myTasks.addAll(taskItems)
        requestModelBuild()
    }

    fun getNumberOfMyTasks(): Int = myTasks.size

    fun deleteItem(index: Int) {
        myTasks.removeAt(index)
        requestModelBuild()
    }

    fun addItem(index: Int, myTask: MyTask) {
        myTasks.add(index, myTask)
        requestModelBuild()
    }
}