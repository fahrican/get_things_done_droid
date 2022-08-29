package com.justluxurylifestyle.get_things_done_droid.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentTaskDetailBinding
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

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
            displayAlertDialog(id)
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
                        "DELETE TASK was successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
                is ViewState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "DELETE TASK couldn't be processed!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun displayAlertDialog(id: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.delete_task_headline))
            .setPositiveButton(
                "delete"
            ) { _, _ -> viewModel.deleteTask(id) }
            .setNegativeButton(
                "cancel"
            ) { _, _ ->
                Toast.makeText(requireContext(), "pressed cancel", Toast.LENGTH_SHORT).show()
            }
        builder.create()
        builder.show()
    }
}