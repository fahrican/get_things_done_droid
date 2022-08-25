package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentCreateTaskBinding
import com.justluxurylifestyle.get_things_done_droid.model.Priority
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem
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


        binding.createTaskBtn.setOnClickListener {
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
            val task = TaskResponseItem(
                description = description,
                timeInterval = timeInterval,
                timeTaken = timeTaken,
                priority = priority,
                isReminderSet = isSetReminderSet,
                createdOn = createdOn,
                isTaskOpen = true
            )
            viewModel.createTask(task)
        }
    }


}