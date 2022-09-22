package com.justluxurylifestyle.get_things_done_droid.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.justluxurylifestyle.get_things_done_droid.TestCoroutineRule
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.rules.TestRule
import androidx.lifecycle.Observer
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.TaskResponseItem
import com.justluxurylifestyle.get_things_done_droid.networking.TaskApi.Companion.ALL_TASKS
import com.justluxurylifestyle.get_things_done_droid.repository.TaskRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.util.ArrayList


@ExperimentalCoroutinesApi
internal class TaskViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @RelaxedMockK
    private lateinit var responseObserver: Observer<ViewState<TaskResponseItem>>

    @RelaxedMockK
    private lateinit var responseObservers: Observer<ViewState<List<TaskResponseItem>>>

    @RelaxedMockK
    private lateinit var responseObserverText: Observer<ViewState<String>>

    @RelaxedMockK
    private lateinit var mockRepo: TaskRepository

    @InjectMockKs
    private lateinit var objectUnderTest: TaskViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        objectUnderTest.task.removeObserver(responseObserver)
        objectUnderTest.tasks.removeObserver(responseObservers)
        objectUnderTest.deleteTaskText.removeObserver(responseObserverText)
    }

    @Test
    fun `when calling for task list then return loading`() {
        coEvery { mockRepo.getTasks(ALL_TASKS) } returns ViewState.Loading

        objectUnderTest.tasks.observeForever(responseObservers)

        objectUnderTest.fetchTasks(ALL_TASKS)

        verify { responseObservers.onChanged(ViewState.Loading) }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when fetching task list is ok then return a response successfully`() {
        val expectedResponse = ArrayList<TaskResponseItem>()

        coEvery { mockRepo.getTasks(ALL_TASKS) } returns ViewState.Success(expectedResponse)

        objectUnderTest.tasks.observeForever(responseObservers)

        objectUnderTest.fetchTasks(ALL_TASKS)

        verifyOrder {
            responseObservers.onChanged(ViewState.Loading)
            responseObservers.onChanged(ViewState.Success(expectedResponse))
        }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when fetching task list fails then return an error`() {
        val exception = mockk<HttpException>()

        coEvery { mockRepo.getTasks(ALL_TASKS) } returns ViewState.Error(exception)

        objectUnderTest.tasks.observeForever(responseObservers)

        objectUnderTest.fetchTasks(ALL_TASKS)

        coVerify {
            responseObservers.onChanged(ViewState.Loading)
            responseObservers.onChanged(ViewState.Error(exception))
        }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when calling create task then return loading`() {
        coEvery { mockRepo.createTask(any()) } returns ViewState.Loading

        objectUnderTest.task.observeForever(responseObserver)

        objectUnderTest.createTask(TaskResponseItem())

        verify { responseObserver.onChanged(ViewState.Loading) }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling create task is ok then return a response successfully`() {
        val task = TaskResponseItem()
        coEvery { mockRepo.createTask(any()) } returns ViewState.Success(task)

        objectUnderTest.task.observeForever(responseObserver)

        objectUnderTest.createTask(task)

        verifyOrder {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Success(task))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling create task fails then return an error`() {
        val exception = mockk<HttpException>()

        coEvery { mockRepo.createTask(any()) } returns ViewState.Error(exception)

        objectUnderTest.task.observeForever(responseObserver)

        objectUnderTest.createTask(TaskResponseItem())

        coVerify {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Error(exception))
        }
        confirmVerified(responseObserver)
    }
}