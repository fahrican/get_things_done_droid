package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentCreateTaskBinding
import com.justluxurylifestyle.get_things_done_droid.model.Priority
import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDateTime


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CreateTaskFragment : ViewBindingFragment<FragmentCreateTaskBinding>() {

    private val viewModel by viewModels<TaskViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateTaskBinding = FragmentCreateTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCreateTaskLiveData()

        binding.createTaskBtn.setOnClickListener {
            val task = createTaskItem()
            viewModel.createTask(task)
        }
    }

    private fun createTaskItem(): TaskCreateRequest {
        val description = binding.createTaskDescriptionInput.text.toString()
        val timeInterval = binding.createTaskTimeIntervalInput.text.toString()
        val timeTaken: Int = Integer.parseInt(binding.createTaskTimeTakenInput.text.toString())
        val isSetReminderSet = binding.createTaskSetReminderCheckBox.isChecked
        val createdOn: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        } else {
            "2022-08-28T23:12:53"
        }
        val priority = if (binding.priorityLow.isChecked) {
            Priority.LOW
        } else if (binding.priorityMedium.isChecked) {
            Priority.MEDIUM
        } else {
            Priority.HIGH
        }
        return TaskCreateRequest(
            description = description,
            priority = priority,
            isReminderSet = isSetReminderSet,
            isTaskOpen = true
        )
    }

    private fun observeCreateTaskLiveData() {
        viewModel.task.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {}
                is ViewState.Success -> {
                    Toast.makeText(
                        requireContext(), TaskApi.REQUEST_SUCCESS, Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
                is ViewState.Error -> {
                    Toast.makeText(
                        requireContext(), TaskApi.REQUEST_FAILURE, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}