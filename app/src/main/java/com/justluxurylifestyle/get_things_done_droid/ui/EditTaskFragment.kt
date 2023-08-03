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
    private val viewModel by viewModels<TaskViewModelImpl>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditTaskBinding = FragmentEditTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTwoWayDataBinding()

        setUpTaskUpdate()
    }

    private fun setUpTwoWayDataBinding() {
        binding.task = args.taskItem
        when (args.taskItem.priority) {
            Priority.LOW -> binding.priorityLow.isChecked = true
            Priority.MEDIUM -> binding.priorityMedium.isChecked = true
            else -> binding.priorityHigh.isChecked = true
        }
    }

    private fun setUpTaskUpdate() {
        binding.editTaskBtn.setOnClickListener {
            val selectedPriority = when (binding.editTaskPriorityRadioGroup.checkedRadioButtonId) {
                R.id.priority_low -> Priority.LOW
                R.id.priority_medium -> Priority.MEDIUM
                R.id.priority_high -> Priority.HIGH
                else -> Priority.LOW
            }
            val updateRequest = TaskUpdateRequest(
                description = binding.editTaskDescriptionInput.text.toString(),
                isReminderSet = binding.editTaskSetReminderCheckBox.isChecked,
                isTaskOpen = binding.editTaskIsTaskOpenBox.isChecked,
                priority = selectedPriority,
                startedOn = null,
                finishedOn = null,
                timeInterval = binding.editTaskTimeIntervalInput.text.toString(),
                timeTaken = binding.editTaskTimeTakenInput.text.toString().toInt()
            )
            lifecycleScope.launch(Dispatchers.Main) {
                async { viewModel.updateTask(args.taskItem.id.toString(), updateRequest) }.await()
                findNavController().popBackStack()
            }
        }
    }
}