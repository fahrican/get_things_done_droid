package com.justluxurylifestyle.get_things_done_droid.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.justluxurylifestyle.get_things_done_droid.TestCoroutineRule
import com.justluxurylifestyle.get_things_done_droid.core.StateOfView
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.Priority
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskCreateRequest
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskFetchResponse
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.dto.TaskUpdateRequest
import com.justluxurylifestyle.get_things_done_droid.repository.TaskRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.HttpException
import retrofit2.Response


@ExperimentalCoroutinesApi
internal class TaskViewModelImplTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @RelaxedMockK
    private lateinit var mockHttpException: HttpException

    @RelaxedMockK
    private lateinit var responseObserver: Observer<StateOfView<TaskFetchResponse>>

    @RelaxedMockK
    private lateinit var responseObservers: Observer<StateOfView<List<TaskFetchResponse>>>

    @RelaxedMockK
    private lateinit var responseObserverDelete: Observer<Boolean>

    @RelaxedMockK
    private lateinit var mockRepo: TaskRepository

    @InjectMockKs
    private lateinit var objectUnderTest: TaskViewModelImpl

    private val createRequest = TaskCreateRequest(
        description = "test data",
        isReminderSet = true,
        isTaskOpen = true,
        priority = Priority.LOW,
        timeInterval = "2 weeks"
    )

    private val updateRequest = TaskUpdateRequest(
        description = "test test",
        isReminderSet = null,
        isTaskOpen = null,
        priority = null,
        startedOn = null,
        finishedOn = null,
        timeInterval = "2 weeks",
        timeTaken = 3
    )

    private val fetchResponse = TaskFetchResponse(
        1,
        "Try new Shawarma place",
        null,
        null,
        null,
        null,
        finishedOn = null,
        timeInterval = null,
        timeTaken = null,
        priority = Priority.LOW
    )


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        objectUnderTest.task.removeObserver(responseObserver)
        objectUnderTest.tasks.removeObserver(responseObservers)
        objectUnderTest.isDeleteSuccessful.removeObserver(responseObserverDelete)
    }

    @Test
    fun `when calling for task list then expect loading state`() {
        // given
        coEvery { mockRepo.getTasks(null) } returns StateOfView.Loading
        objectUnderTest.tasks.observeForever(responseObservers)

        // when
        objectUnderTest.fetchTasks(null)

        // then
        verify { responseObservers.onChanged(StateOfView.Loading) }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when fetching task list then expect a successful response`() {
        // given
        val expectedResponse = ArrayList<TaskFetchResponse>()
        coEvery { mockRepo.getTasks(null) } returns StateOfView.Success(expectedResponse)
        objectUnderTest.tasks.observeForever(responseObservers)

        // when
        objectUnderTest.fetchTasks(null)

        // then
        verifyOrder {
            responseObservers.onChanged(StateOfView.Loading)
            responseObservers.onChanged(StateOfView.Success(expectedResponse))
        }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when fetching task list then expect an http exception`() {
        // given
        coEvery { mockRepo.getTasks(null) } returns StateOfView.Error(mockHttpException)
        objectUnderTest.tasks.observeForever(responseObservers)

        // when
        objectUnderTest.fetchTasks(null)

        // then
        coVerify {
            responseObservers.onChanged(StateOfView.Loading)
            responseObservers.onChanged(StateOfView.Error(mockHttpException))
        }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when calling for a task with id then expect loading state`() {
        // given
        coEvery { mockRepo.getTaskById("1") } returns StateOfView.Loading
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.fetchTaskById("1")

        // then
        verify { responseObserver.onChanged(StateOfView.Loading) }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling for a task with id then expect successful response`() {
        // given
        coEvery { mockRepo.getTaskById("1") } returns StateOfView.Success(fetchResponse)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.fetchTaskById("1")

        // then
        verify {
            responseObserver.onChanged(StateOfView.Loading)
            responseObserver.onChanged(StateOfView.Success(fetchResponse))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when fetching for a task with id then expect an http exception`() {
        // given
        coEvery { mockRepo.getTaskById("2") } returns StateOfView.Error(mockHttpException)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.fetchTaskById("2")

        // then
        coVerify {
            responseObserver.onChanged(StateOfView.Loading)
            responseObserver.onChanged(StateOfView.Error(mockHttpException))
        }
        confirmVerified(responseObserver)
    }


    @Test
    fun `when calling create task then expect loading state`() {
        // given
        coEvery { mockRepo.createTask(any()) } returns StateOfView.Loading
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.createTask(createRequest)

        // then
        verify { responseObserver.onChanged(StateOfView.Loading) }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling create task then expect successful response`() {
        // given
        coEvery { mockRepo.createTask(any()) } returns StateOfView.Success(fetchResponse)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.createTask(createRequest)

        // then
        verifyOrder {
            responseObserver.onChanged(StateOfView.Loading)
            responseObserver.onChanged(StateOfView.Success(fetchResponse))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling create task then expect an http exception`() {
        // given
        coEvery { mockRepo.createTask(any()) } returns StateOfView.Error(mockHttpException)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.createTask(createRequest)

        // then
        coVerify {
            responseObserver.onChanged(StateOfView.Loading)
            responseObserver.onChanged(StateOfView.Error(mockHttpException))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling update task then expect loading state`() {
        // given
        coEvery { mockRepo.updateTask(any(), any()) } returns StateOfView.Loading
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.updateTask("3", updateRequest)

        // then
        verify { responseObserver.onChanged(StateOfView.Loading) }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling update task then expect successful response`() {
        // given
        coEvery { mockRepo.updateTask(any(), any()) } returns StateOfView.Success(fetchResponse)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.updateTask("1", updateRequest)

        // then
        verifyOrder {
            responseObserver.onChanged(StateOfView.Loading)
            responseObserver.onChanged(StateOfView.Success(fetchResponse))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling update task then expect an http exception`() {
        // given
        coEvery { mockRepo.updateTask(any(), any()) } returns StateOfView.Error(mockHttpException)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.updateTask("1", updateRequest)

        // then
        coVerify {
            responseObserver.onChanged(StateOfView.Loading)
            responseObserver.onChanged(StateOfView.Error(mockHttpException))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling delete task then expect successful response`() {
        // given
        val isSuccess = Response.success(true)
        coEvery { mockRepo.canDeleteTask("4") } returns StateOfView.Success(isSuccess)
        objectUnderTest.isDeleteSuccessful.observeForever(responseObserverDelete)

        // when
        objectUnderTest.deleteTask("4")


        // then
        verifyOrder { responseObserverDelete.onChanged(true) }
        confirmVerified(responseObserverDelete)
    }

    @Test
    fun `when calling delete task then expect http exception`() {
        // given
        coEvery { mockRepo.canDeleteTask("4") } returns StateOfView.Error(mockHttpException)
        objectUnderTest.isDeleteSuccessful.observeForever(responseObserverDelete)

        // when
        objectUnderTest.deleteTask("4")

        // then
        coVerify {
            responseObserverDelete.onChanged(false)
            mockRepo.canDeleteTask("4")
        }
        confirmVerified(responseObserverDelete)
    }
}