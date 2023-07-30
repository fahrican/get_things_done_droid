package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.core.StateOfView
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentTaskDetailBinding
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.ui.util.displayAlertDialog
import com.justluxurylifestyle.get_things_done_droid.ui.util.showToastMessage
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskDetailFragment : ViewBindingFragment<FragmentTaskDetailBinding>() {

    private val args: TaskDetailFragmentArgs by navArgs()
    private val viewModel by viewModels<TaskViewModelImpl>()
    private var fetchResponse: TaskFetchResponse? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskDetailBinding = FragmentTaskDetailBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeTaskLiveData()

        observeDeleteTaskLiveData()

        setUpDeleteTask()

        setUpEditTask()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchTaskById(args.taskId.toString())
    }

    private fun observeTaskLiveData() {
        viewModel.task.observe(viewLifecycleOwner) { response ->
            when (response) {
                is StateOfView.Loading -> {
                    showLoadingState()
                }

                is StateOfView.Success -> {
                    handleSuccessState(response.data)
                }

                is StateOfView.Error -> {
                    handleErrorState(response.exception.message.toString())
                }
            }
        }
    }

    private fun observeDeleteTaskLiveData() {
        viewModel.isDeleteSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                showToastMessage(requireContext(), getString(R.string.task_request_success_message))
                findNavController().popBackStack()
            } else {
                showToastMessage(requireContext(), getString(R.string.task_request_failure_message))
                Timber.d("Delete status: $isSuccessful")
            }
        }
    }

    private fun showLoadingState() {
        binding.shimmerFrame.startShimmerAnimation()
    }

    private fun handleSuccessState(taskFetchResponse: TaskFetchResponse) {
        fetchResponse = taskFetchResponse.apply { onClick = null }
        binding.task = fetchResponse
        binding.shimmerFrame.stopShimmerAnimation()
        binding.taskDetailErrorText.visibility = View.GONE
    }

    private fun handleErrorState(errorMessage: String) {
        with(binding) {
            shimmerFrame.apply {
                stopShimmerAnimation()
                visibility = View.GONE
            }
            taskDetailErrorText.apply {
                text = errorMessage
                visibility = View.VISIBLE
            }
        }
    }

    private fun setUpEditTask() {
        binding.taskDetailEditTaskBtn.setOnClickListener {
            fetchResponse?.let {
                val action = TaskDetailFragmentDirections.actionTaskDetailToEditTask(it)
                findNavController().navigate(action)
            }
        }
    }

    private fun setUpDeleteTask() {
        binding.taskDetailDeleteTaskBtn.setOnClickListener {
            fetchResponse?.let {
                displayAlertDialog(
                    it.id.toString(),
                    requireContext(),
                    getString(R.string.delete_task_headline),
                    viewModel
                )
            }
        }
    }
}
