package com.justluxurylifestyle.get_things_done_droid.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.justluxurylifestyle.get_things_done_droid.TestCoroutineRule
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.rules.TestRule
import androidx.lifecycle.Observer
import com.justluxurylifestyle.get_things_done_droid.core.ViewState
import com.justluxurylifestyle.get_things_done_droid.model.Priority
import com.justluxurylifestyle.get_things_done_droid.model.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.model.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.model.TaskUpdateRequest
import com.justluxurylifestyle.get_things_done_droid.repository.TaskRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.util.ArrayList


@ExperimentalCoroutinesApi
internal class TaskViewModelImplTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @RelaxedMockK
    private lateinit var responseObserver: Observer<ViewState<TaskFetchResponse>>

    @RelaxedMockK
    private lateinit var responseObservers: Observer<ViewState<List<TaskFetchResponse>>>

    @RelaxedMockK
    private lateinit var responseObserverText: Observer<ViewState<String>>

    @RelaxedMockK
    private lateinit var responseObserverUnit: Observer<ViewState<Response<Unit>>>

    @RelaxedMockK
    private lateinit var mockRepo: TaskRepository

    @InjectMockKs
    private lateinit var objectUnderTest: TaskViewModelImpl

    private val createRequest = TaskCreateRequest(
        "test data",
        isReminderSet = true,
        isTaskOpen = true,
        priority = Priority.HIGH
    )

    private val updateRequest = TaskUpdateRequest(
        "new update",
        isReminderSet = false,
        isTaskOpen = false,
        priority = Priority.LOW
    )

    private val fetchResponse = TaskFetchResponse(
        1,
        "test data",
        null,
        null,
        null,
        null
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        objectUnderTest.task.removeObserver(responseObserver)
        objectUnderTest.tasks.removeObserver(responseObservers)
        objectUnderTest.deleteTaskText.removeObserver(responseObserverUnit)
    }

    @Test
    fun `when calling for task list then return loading`() {
        coEvery { mockRepo.getTasks(null) } returns ViewState.Loading

        objectUnderTest.tasks.observeForever(responseObservers)

        objectUnderTest.fetchTasks(null)

        verify { responseObservers.onChanged(ViewState.Loading) }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when fetching task list is ok then return a response successfully`() {
        val expectedResponse = ArrayList<TaskFetchResponse>()

        coEvery { mockRepo.getTasks(null) } returns ViewState.Success(expectedResponse)

        objectUnderTest.tasks.observeForever(responseObservers)

        objectUnderTest.fetchTasks(null)

        verifyOrder {
            responseObservers.onChanged(ViewState.Loading)
            responseObservers.onChanged(ViewState.Success(expectedResponse))
        }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when fetching task list fails then return an error`() {
        val exception = mockk<HttpException>()

        coEvery { mockRepo.getTasks(null) } returns ViewState.Error(exception)

        objectUnderTest.tasks.observeForever(responseObservers)

        objectUnderTest.fetchTasks(null)

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

        objectUnderTest.createTask(createRequest)

        verify { responseObserver.onChanged(ViewState.Loading) }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling create task is ok then return a response successfully`() {
        coEvery { mockRepo.createTask(any()) } returns ViewState.Success(fetchResponse)

        objectUnderTest.task.observeForever(responseObserver)

        objectUnderTest.createTask(createRequest)

        verifyOrder {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Success(fetchResponse))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling create task fails then return an error`() {
        val exception = mockk<HttpException>()

        coEvery { mockRepo.createTask(any()) } returns ViewState.Error(exception)

        objectUnderTest.task.observeForever(responseObserver)

        objectUnderTest.createTask(createRequest)

        coVerify {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Error(exception))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling delete task then return loading`() {
        coEvery { mockRepo.deleteTask(any()) } returns ViewState.Loading

        objectUnderTest.deleteTaskText.observeForever(responseObserverUnit)

        objectUnderTest.deleteTask("15")

        verify { responseObserverText.onChanged(ViewState.Loading) }
        confirmVerified(responseObserverText)
    }

    @Test
    fun `when calling delete task fails then return an error`() {
        val exception = mockk<HttpException>()

        coEvery { mockRepo.deleteTask(any()) } returns ViewState.Error(exception)

        objectUnderTest.deleteTaskText.observeForever(responseObserverUnit)

        objectUnderTest.deleteTask("15")

        coVerify {
            responseObserverText.onChanged(ViewState.Loading)
            responseObserverText.onChanged(ViewState.Error(exception))
        }
        confirmVerified(responseObserverText)
    }

    @Test
    fun `when calling update task then return loading`() {
        coEvery { mockRepo.updateTask(any(), any()) } returns ViewState.Loading

        objectUnderTest.task.observeForever(responseObserver)

        objectUnderTest.updateTask("3", updateRequest)

        verify { responseObserver.onChanged(ViewState.Loading) }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling update task is ok then return a response successfully`() {
        coEvery { mockRepo.updateTask(any(), any()) } returns ViewState.Success(fetchResponse)

        objectUnderTest.task.observeForever(responseObserver)

        objectUnderTest.updateTask("1", updateRequest)

        verifyOrder {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Success(fetchResponse))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling update task fails then return an error`() {
        val exception = mockk<HttpException>()

        coEvery { mockRepo.updateTask(any(), any()) } returns ViewState.Error(exception)

        objectUnderTest.task.observeForever(responseObserver)

        objectUnderTest.updateTask("1", updateRequest)

        coVerify {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Error(exception))
        }
        confirmVerified(responseObserver)
    }
}