package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.core.StateOfView
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentCreateTaskBinding
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.Priority
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.ui.util.showToastMessage
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskCreateFragment : ViewBindingFragment<FragmentCreateTaskBinding>() {

    private val viewModel by viewModels<TaskViewModelImpl>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateTaskBinding = FragmentCreateTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCreateTaskLiveData()

        binding.createTaskBtn.setOnClickListener {
            val task = createTaskItem()
            task?.let { viewModel.createTask(it) }
        }
    }

    private fun createTaskItem(): TaskCreateRequest? {
        val description = binding.createTaskDescriptionInput.text.toString()
        val timeInterval = binding.createTaskTimeIntervalInput.text.toString()
        val isSetReminderSet = binding.createTaskSetReminderCheckBox.isChecked

        if (description.isEmpty()) {
            showToastMessage(requireContext(), getString(R.string.enter_description))
            return null
        }

        val priority = when {
            binding.priorityLow.isChecked -> Priority.LOW
            binding.priorityMedium.isChecked -> Priority.MEDIUM
            else -> Priority.HIGH
        }

        return TaskCreateRequest(
            description = description,
            isReminderSet = isSetReminderSet,
            isTaskOpen = true,
            timeInterval = timeInterval,
            priority = priority
        )
    }

    private fun observeCreateTaskLiveData() {
        viewModel.task.observe(viewLifecycleOwner) { response ->
            when (response) {
                is StateOfView.Loading -> {
                    Timber.d("data is loading ${response.extractData}")
                }

                is StateOfView.Success -> {
                    showToastMessage(
                        requireContext(),
                        getString(R.string.task_request_success_message)
                    )
                    findNavController().popBackStack()
                }

                is StateOfView.Error -> {
                    val errorMessage = response.exception.message
                        ?: getString(R.string.task_request_failure_message)
                    showToastMessage(requireContext(), errorMessage)
                }
            }
        }
    }
}