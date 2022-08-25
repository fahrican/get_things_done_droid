package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.viewModels
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentCreateTaskBinding
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber


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
            val timeTaken = binding.createTaskTimeTakenInput.text
            val isSetReminderSet = binding.createTaskSetReminderCheckBox.isChecked
            Timber.d(
                "CreateTaskFragment -> description: $description & timeInterval: $timeInterval & timeTaken: $timeTaken " +
                        "& priorityLow: ${binding.priorityLow.isChecked} & priorityMedium: ${binding.priorityMedium.isChecked}} & priorityHigh: ${binding.priorityHigh.isChecked}} & isSetReminderSet: $isSetReminderSet"
            )
            //val task = TaskResponseItem(description = binding.textInputDescription.get)
            //viewModel.createTask()
        }
    }


}