package com.justluxurylifestyle.get_things_done_droid.ui.open_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.snackbar.Snackbar
import com.justluxurylifestyle.get_things_done_droid.core.ViewBindingFragment
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.databinding.FragmentOpenTaskBinding
import com.justluxurylifestyle.get_things_done_droid.model.MyTask
import com.justluxurylifestyle.get_things_done_droid.ui.view.epoxy.TaskController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OpenTaskFragment : ViewBindingFragment<FragmentOpenTaskBinding>() {

    private val viewModel by viewModels<OpenTaskViewModel>()
    private val controller = TaskController()
    private val myTasks = mutableListOf<MyTask>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOpenTaskBinding = FragmentOpenTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpViewModel()

       setUpRecyclerView()

        observeLiveData()

        clickOnRetry()
    }

    private fun setUpViewModel() {
        viewModel.fetchOpenTasks()
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
                    binding.articleFetchProgress.visibility = View.VISIBLE
                }
                is ViewState.Success -> {
                    if (response.data.isEmpty()) {
                        showEmptyScreen()
                    } else {
                        showArticlesOnScreen()
                    }
                    response.data.let { tasks ->
                        tasks.forEach { task ->
                            val taskItems = MyTask(task)
                            taskItems.onClick = View.OnClickListener {
                                val action = OpenTaskFragmentDirections.actionOpenTaskToTaskDetail()
                                findNavController().navigate(action)
                            }
                            this.myTasks.add(taskItems)
                        }
                        this.controller.setTasks(myTasks)
                    }
                    binding.articleFetchProgress.visibility = View.GONE
                    //binding.swipeRefresh.isRefreshing = false
                }
                is ViewState.Error -> {
                    showEmptyScreen()
                }
            }
        }
    }

    private fun showEmptyScreen() {
        controller.setTasks(emptyList())
        binding.articleFetchProgress.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyText.visibility = View.VISIBLE
        binding.retryFetchButton.visibility = View.VISIBLE
    }

    private fun showArticlesOnScreen() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.emptyText.visibility = View.GONE
        binding.retryFetchButton.visibility = View.GONE
    }

    private fun clickOnRetry() {
        binding.retryFetchButton.setOnClickListener {
            binding.emptyText.visibility = View.GONE
            it.visibility = View.GONE
            binding.articleFetchProgress.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.Main) {
                val response = async { viewModel.fetchOpenTasks() }
                response.await()
                if (controller.getNumberOfMyTasks() == 0) {
                    Snackbar.make(
                        it,
                        "No, tasks found",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


}