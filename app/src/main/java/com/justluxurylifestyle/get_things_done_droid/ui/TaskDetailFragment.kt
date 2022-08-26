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
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentTaskDetailBinding
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

        binding.deleteTaskBtn.setOnClickListener {
            displayAlertDialog(id)
        }

        binding.editTaskBtn.setOnClickListener {
            val action = TaskDetailFragmentDirections.actionTaskDetailToEditTask(args.taskItem)
            findNavController().navigate(action)
        }
    }

    private fun displayAlertDialog(id: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.delete_task_headline))
            .setPositiveButton(
                "delete"
            ) { _, _ ->
                lifecycleScope.launch(Dispatchers.Main) {
                    async { viewModel.deleteTask(id) }.await()
                    findNavController().popBackStack()
                }
                Toast.makeText(requireContext(), "pressed delete", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(
                "cancel"
            ) { _, _ ->
                Toast.makeText(requireContext(), "pressed cancel", Toast.LENGTH_SHORT).show()
            }
        builder.create()
        builder.show()
    }
}