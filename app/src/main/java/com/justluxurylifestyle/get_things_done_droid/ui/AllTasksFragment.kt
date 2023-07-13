package com.justluxurylifestyle.get_things_done_droid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentTaskBinding
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.ui.view.epoxy.TaskController
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AllTasksFragment : ViewBindingFragment<FragmentTaskBinding>(),
    SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by viewModels<TaskViewModelImpl>()
    private lateinit var controller: TaskController
    private val myTasks = mutableListOf<TaskFetchResponse>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskBinding = FragmentTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val color = ContextCompat.getColor(requireActivity(), R.color.black)
        controller = TaskController(color)
        binding.fabLayout.visibility = View.GONE

        callViewModel()

        setUpRecyclerView()

        observeLiveData()

        clickOnRetry()

        setUpSwipeRefresh()

        setUpRetryButton()
    }


    override fun onPause() {
        super.onPause()
        binding.swipeRefresh.isRefreshing = false
        binding.swipeRefresh.clearAnimation()
        binding.swipeRefresh.clearFocus()
        binding.swipeRefresh.setOnRefreshListener(null)
    }

    override fun onStop() {
        super.onStop()
        binding.retryFetchButton.setOnClickListener(null)
    }

    //From SwipeRefreshLayout
    override fun onRefresh() {
        callViewModel()
    }

    private fun callViewModel() {
        viewModel.fetchTasks(null)
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

    private fun observeLiveData() {
        viewModel.tasks.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.shimmerFrame.startShimmerAnimation()
                }

                is ViewState.Success -> {
                    this.myTasks.clear()
                    if (response.data.isEmpty()) {
                        showEmptyScreen()
                    } else {
                        showArticlesOnScreen()
                    }
                    response.data.let { tasks ->
                        tasks.forEach { task ->
                            task.onClick = View.OnClickListener {
                                val action =
                                    AllTasksFragmentDirections.actionAllTasksToTaskDetail(task.id)
                                findNavController().navigate(action)
                                // Check for no tasks after data is loaded.
                                if (controller.getNumberOfMyTasks() == 0) {
                                    Snackbar.make(
                                        requireView(),
                                        "No, tasks found",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            this.myTasks.add(task)
                        }
                        this.controller.setTasks(myTasks)
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
            recyclerView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
            retryFetchButton.visibility = View.GONE
        }
    }

    private fun clickOnRetry() {
        binding.retryFetchButton.setOnClickListener {
            binding.emptyText.visibility = View.GONE
            it.visibility = View.GONE
            binding.shimmerFrame.startShimmerAnimation()
            callViewModel()
            if (controller.getNumberOfMyTasks() == 0) {
                Snackbar.make(
                    it,
                    "No, tasks found",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setUpRetryButton() {
        binding.retryFetchButton.setOnClickListener {
            binding.emptyText.visibility = View.GONE
            it.visibility = View.GONE
            binding.shimmerFrame.startShimmerAnimation()
            callViewModel()
        }
    }
}