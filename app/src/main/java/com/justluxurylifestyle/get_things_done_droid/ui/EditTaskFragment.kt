package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentEditTaskBinding
import com.justluxurylifestyle.get_things_done_droid.model.Priority
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditTaskFragment : ViewBindingFragment<FragmentEditTaskBinding>() {

    private val args: EditTaskFragmentArgs by navArgs()
    private lateinit var userPriority: Priority
    private val viewModel by viewModels<TaskViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditTaskBinding = FragmentEditTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTwoWayDataBinding()
        binding.editTaskBtn.setOnClickListener {
            val task = TaskResponseItem(
                id = args.taskItem.id,
                description = binding.editTaskDescriptionInput.text.toString(),
                timeInterval = binding.editTaskTimeIntervalInput.text.toString(),
                timeTaken = Integer.parseInt(binding.editTaskTimeTakenInput.text.toString()),
                priority = userPriority,
                isReminderSet = binding.editTaskSetReminderCheckBox.isChecked,
                createdOn = binding.editTaskCreatedOnInput.text.toString(),
                isTaskOpen = binding.editTaskIsTaskOpenBox.isChecked,
                finishedOn = binding.editTaskFinishedOnInput.text.toString(),
                startedOn = binding.editTaskStartedOnInput.text.toString(),
            )
            lifecycleScope.launch(Dispatchers.Main) {
                async { viewModel.updateTask(task) }
                findNavController().popBackStack()
            }
        }
    }

    private fun setUpTwoWayDataBinding() {
        binding.task = args.taskItem
        args.taskItem.isTaskOpen?.let { isOpen -> binding.editTaskIsTaskOpenBox.isChecked = isOpen }
        args.taskItem.isReminderSet?.let { isReminderSet ->
            binding.editTaskSetReminderCheckBox.isChecked = isReminderSet
        }
        when (args.taskItem.priority) {
            Priority.LOW -> {
                binding.priorityLow.isChecked = true
                userPriority = Priority.LOW
            }
            Priority.MEDIUM -> {
                binding.priorityMedium.isChecked = true
                userPriority = Priority.MEDIUM
            }
            else -> {
                binding.priorityHigh.isChecked = true
                userPriority = Priority.HIGH
            }
        }
    }
}