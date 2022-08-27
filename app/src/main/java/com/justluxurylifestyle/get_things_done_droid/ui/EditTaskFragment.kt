package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentEditTaskBinding
import com.justluxurylifestyle.get_things_done_droid.model.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditTaskFragment : ViewBindingFragment<FragmentEditTaskBinding>() {

    private val args: EditTaskFragmentArgs by navArgs()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditTaskBinding = FragmentEditTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.task = args.taskItem
        args.taskItem.isTaskOpen?.let { isOpen -> binding.editTaskIsTaskOpenBox.isChecked = isOpen }
        args.taskItem.isReminderSet?.let { isReminderSet ->
            binding.editTaskIsTaskOpenBox.isChecked = isReminderSet
        }
        when (args.taskItem.priority) {
            Priority.LOW -> binding.priorityLow.isChecked = true
            Priority.MEDIUM -> binding.priorityMedium.isChecked = true
            else -> binding.priorityHigh.isChecked = true
        }
    }
}