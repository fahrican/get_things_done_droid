package com.justluxurylifestyle.get_things_done_droid.ui.bottom.nav

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.model.TaskStatus
import com.justluxurylifestyle.get_things_done_droid.ui.view.epoxy.TaskController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskOpenFragment : TaskFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCreateTaskButton()
    }

    override fun onResume() {
        super.onResume()
        callViewModel()
    }

    override fun initializeController() {
        val color = ContextCompat.getColor(requireActivity(), R.color.darker_gray)
        controller = TaskController(color)
    }

    override fun callViewModel() {
        viewModel.fetchTasks(TaskStatus.OPEN.toString())
    }

    override fun navigateToEditTask(task: TaskFetchResponse) {
        val action =
            TaskOpenFragmentDirections.actionOpenTaskToEditTask(task)
        findNavController().navigate(action)
    }

    override fun navigateToTaskDetail(task: TaskFetchResponse) {
        val action =
            TaskOpenFragmentDirections.actionOpenTaskToTaskDetail(task.id)
        findNavController().navigate(action)
    }

    private fun setUpCreateTaskButton() {
        binding.fabBtn.setOnClickListener {
            val action = TaskOpenFragmentDirections.actionOpenTaskToCreateTask()
            findNavController().navigate(action)
        }
    }
}
