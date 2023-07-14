package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentTaskBinding
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.model.TaskStatus
import com.justluxurylifestyle.get_things_done_droid.ui.message.displayAlertDialog
import com.justluxurylifestyle.get_things_done_droid.ui.message.showToastMessage
import com.justluxurylifestyle.get_things_done_droid.ui.view.epoxy.SwipeGestures
import com.justluxurylifestyle.get_things_done_droid.ui.view.epoxy.TaskController
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OpenTaskFragment : ViewBindingFragment<FragmentTaskBinding>(),
    SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by viewModels<TaskViewModelImpl>()
    private lateinit var controller: TaskController
    private val tasks = mutableListOf<TaskFetchResponse>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskBinding = FragmentTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwipeGestures()
        initializeController()
        callViewModel()
        setUpRecyclerView()
        observeTaskLiveData()
        observeDeleteTaskLiveData()
        clickOnRetry()
        setUpSwipeRefresh()
        setUpCreateTaskButton()
    }

    private fun setUpCreateTaskButton() {
        binding.fabBtn.setOnClickListener {
            val action = OpenTaskFragmentDirections.actionOpenTaskToCreateTask()
            findNavController().navigate(action)
        }
    }

    private fun setupSwipeGestures() {
        val swipeGestures = object : SwipeGestures(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val myTask = controller.getTaskById(viewHolder.absoluteAdapterPosition)
                myTask.let { task ->
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            displayAlertDialog(
                                task.id.toString(),
                                requireContext(),
                                getString(R.string.delete_task_headline),
                                viewModel
                            )
                            controller.notifyModelChanged(viewHolder.absoluteAdapterPosition)
                        }

                        ItemTouchHelper.RIGHT -> {
                            val action =
                                OpenTaskFragmentDirections.actionOpenTaskToEditTask(task)
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGestures)
        touchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun initializeController() {
        val color = ContextCompat.getColor(requireActivity(), R.color.darker_gray)
        controller = TaskController(color)
    }

    override fun onPause() {
        super.onPause()
        with(binding) {
            swipeRefresh.isRefreshing = false
            swipeRefresh.clearAnimation()
            swipeRefresh.clearFocus()
            swipeRefresh.setOnRefreshListener(null)
        }
    }

    override fun onRefresh() {
        callViewModel()
    }

    private fun callViewModel() {
        viewModel.fetchTasks(TaskStatus.OPEN.toString())
    }

    private fun setUpSwipeRefresh() {
        binding.swipeRefresh.let {
            it.setOnRefreshListener(this)
            it.setColorSchemeResources(R.color.purple_200)
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            this.setHasFixedSize(true)
            this.itemAnimator = DefaultItemAnimator()
            this.adapter = controller.adapter
        }
    }

    private fun observeTaskLiveData() {
        viewModel.tasks.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.shimmerFrame.startShimmerAnimation()
                }

                is ViewState.Success -> {
                    this.tasks.clear()
                    if (response.data.isEmpty()) {
                        showEmptyScreen()
                    } else {
                        showArticlesOnScreen()
                    }
                    val fetchedTasks = response.data.map { task ->
                        task.onClick = View.OnClickListener {
                            val action =
                                OpenTaskFragmentDirections.actionOpenTaskToTaskDetail(task.id)
                            findNavController().navigate(action)
                        }
                        task
                    }
                    this.tasks.addAll(fetchedTasks)
                    this.controller.setTasks(this.tasks)

                    if (controller.getNumberOfMyTasks() == 0) {
                        Snackbar.make(
                            requireView(),
                            getString(R.string.no_tasks_found),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                is ViewState.Error -> {
                    showEmptyScreen()
                }
            }
        }
    }

    private fun showEmptyScreen() {
        with(binding) {
            shimmerFrame.stopShimmerAnimation()
            shimmerFrame.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
            retryFetchButton.visibility = View.VISIBLE
        }
        controller.setTasks(emptyList())
    }

    private fun showArticlesOnScreen() {
        with(binding) {
            shimmerFrame.stopShimmerAnimation()
            shimmerFrame.visibility = View.GONE
            emptyText.visibility = View.GONE
            retryFetchButton.visibility = View.GONE
            swipeRefresh.isRefreshing = false
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun observeDeleteTaskLiveData() {
        viewModel.isDeleteSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                showToastMessage(requireContext(), getString(R.string.task_request_success_message))
                callViewModel()
            } else {
                showToastMessage(requireContext(), getString(R.string.task_request_failure_message))
                Timber.d("Delete status: $isSuccessful")
            }
        }
    }

    private fun clickOnRetry() {
        with(binding) {
            retryFetchButton.setOnClickListener { button ->
                button.visibility = View.GONE
                emptyText.visibility = View.GONE
                shimmerFrame.startShimmerAnimation()
                shimmerFrame.visibility = View.VISIBLE

                callViewModel()
            }
        }
    }
}