package com.justluxurylifestyle.get_things_done_droid.ui.bottom.nav

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
class ClosedTaskFragment : TaskFragment() {

    override fun onResume() {
        super.onResume()
        callViewModel()
    }

    override fun initializeController() {
        val color = ContextCompat.getColor(requireActivity(), R.color.green_dark)
        controller = TaskController(color)
        binding.fabLayout.visibility = View.GONE
    }

    override fun callViewModel() {
        viewModel.fetchTasks(TaskStatus.CLOSED.toString())
    }

    override fun navigateToEditTask(task: TaskFetchResponse) {
        val action =
            ClosedTaskFragmentDirections.actionClosedTaskToEditTask(task)
        findNavController().navigate(action)
    }

    override fun navigateToTaskDetail(task: TaskFetchResponse) {
        val action =
            ClosedTaskFragmentDirections.actionClosedTaskToTaskDetail(task.id)
        findNavController().navigate(action)
    }
}
