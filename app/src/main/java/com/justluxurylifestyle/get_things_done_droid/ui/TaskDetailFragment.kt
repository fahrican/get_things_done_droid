package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentTaskDetailBinding
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi
import com.justluxurylifestyle.get_things_done_droid.ui.dialog.displayAlertDialog
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskDetailFragment : ViewBindingFragment<FragmentTaskDetailBinding>() {

    private val args: TaskDetailFragmentArgs by navArgs()
    private val viewModel by viewModels<TaskViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskDetailBinding = FragmentTaskDetailBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.task = args.taskItem
        val id: String = args.taskItem.id.toString()

        observeDeleteTaskLiveData()

        binding.deleteTaskBtn.setOnClickListener {
            displayAlertDialog(
                id,
                requireContext(),
                getString(R.string.delete_task_headline),
                viewModel
            )
        }

        binding.editTaskBtn.setOnClickListener {
            val action = TaskDetailFragmentDirections.actionTaskDetailToEditTask(args.taskItem)
            findNavController().navigate(action)
        }
    }

    private fun observeDeleteTaskLiveData() {
        viewModel.deleteTaskText.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {}
                is ViewState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        TaskApi.REQUEST_SUCCESS,
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
                is ViewState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        TaskApi.REQUEST_FAILURE,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}