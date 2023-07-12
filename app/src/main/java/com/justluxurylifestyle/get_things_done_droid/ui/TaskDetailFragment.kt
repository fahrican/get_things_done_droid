package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentTaskDetailBinding
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi
import com.justluxurylifestyle.get_things_done_droid.ui.dialog.displayAlertDialog
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskDetailFragment : ViewBindingFragment<FragmentTaskDetailBinding>() {

    private val args: TaskDetailFragmentArgs by navArgs()
    private val viewModel by viewModels<TaskViewModelImpl>()
    private lateinit var fetchResponse: TaskFetchResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchTaskById(args.taskId.toString())
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskDetailBinding = FragmentTaskDetailBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()

        observeDeleteTaskLiveData()

        binding.taskDetailDeleteTaskBtn.setOnClickListener {
            displayAlertDialog(
                fetchResponse.id.toString(),
                requireContext(),
                getString(R.string.delete_task_headline),
                viewModel
            )
        }

        binding.taskDetailEditTaskBtn.setOnClickListener {
            val action = TaskDetailFragmentDirections.actionTaskDetailToEditTask(fetchResponse)
            findNavController().navigate(action)
        }
    }

    private fun observeLiveData() {
        viewModel.task.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.shimmerFrame.startShimmerAnimation()
                }

                is ViewState.Success -> {
                    response.data.let { task ->
                        fetchResponse = task
                        fetchResponse.onClick = null
                    }
                    binding.task = fetchResponse
                    binding.shimmerFrame.stopShimmerAnimation()
                    binding.taskDetailErrorText.visibility = View.GONE
                }

                is ViewState.Error -> {
                    showEmptyScreen()
                }
            }
        }
    }

    private fun observeDeleteTaskLiveData() {
        viewModel.deleteTaskText.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        TaskApi.REQUEST_SUCCESS,
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }

                is ViewState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        TaskApi.REQUEST_FAILURE,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Unknown delete state",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showEmptyScreen() {
        binding.shimmerFrame.stopShimmerAnimation()
        binding.shimmerFrame.visibility = View.GONE
        binding.taskDetailErrorText.visibility = View.VISIBLE
    }
}