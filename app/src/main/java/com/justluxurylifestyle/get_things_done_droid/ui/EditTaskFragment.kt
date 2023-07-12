package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentEditTaskBinding
import com.justluxurylifestyle.get_things_done_droid.model.Priority
import com.justluxurylifestyle.get_things_done_droid.model.TaskUpdateRequest
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModelImpl
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
    private val viewModel by viewModels<TaskViewModelImpl>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditTaskBinding = FragmentEditTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTwoWayDataBinding()
        binding.editTaskBtn.setOnClickListener {
            val selectedPriority = when (binding.editTaskPriorityRadioGroup.checkedRadioButtonId) {
                R.id.priority_low -> Priority.LOW
                R.id.priority_medium -> Priority.MEDIUM
                R.id.priority_high -> Priority.HIGH
                else -> Priority.LOW // You can define a default value here, for when none of the radio buttons are selected.
            }
            val updateRequest = TaskUpdateRequest(
                description = binding.editTaskDescriptionInput.text.toString(),
                priority = selectedPriority,
                isReminderSet = binding.editTaskSetReminderCheckBox.isChecked,
                isTaskOpen = binding.editTaskIsTaskOpenBox.isChecked,
            )
            lifecycleScope.launch(Dispatchers.Main) {
                async { viewModel.updateTask(args.taskItem.id.toString(), updateRequest) }.await()
                val action = EditTaskFragmentDirections.actionEditTaskToTaskDetail(args.taskItem.id)
                findNavController().navigate(action)
                findNavController().popBackStack()
            }
        }
    }

    private fun setUpTwoWayDataBinding() {
        binding.task = args.taskItem
        userPriority = args.taskItem.priority ?: Priority.LOW
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