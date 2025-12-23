package com.aaltix.lotto.feature.history.presentation.detail

import app.cash.turbine.test
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.feature.history.util.MainDispatcherRule
import com.aaltix.lotto.feature.history.util.TestDispatcherProvider
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.usecase.DeleteHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.GetHistoryDetailUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HistoryDetailViewModel
    private lateinit var getHistoryDetailUseCase: GetHistoryDetailUseCase
    private lateinit var deleteHistoryUseCase: DeleteHistoryUseCase
    private lateinit var dispatcherProvider: DispatcherProvider

    private val testLotteryType = LotteryType(
        id = "powerball",
        name = "Powerball",
        displayName = "Powerball",
        mainNumberCount = 5,
        mainNumberMax = 69,
        bonusNumberCount = 1,
        bonusNumberMax = 26
    )

    private val testEntry = GeneratedNumbers(
        id = "test-entry-id",
        lotteryType = testLotteryType,
        mainNumbers = listOf(5, 12, 23, 45, 67),
        bonusNumbers = listOf(15),
        timestamp = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        getHistoryDetailUseCase = mockk()
        deleteHistoryUseCase = mockk()
        dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher)

        coEvery { getHistoryDetailUseCase(any()) } returns Result.Success(testEntry)
        coEvery { deleteHistoryUseCase(any()) } returns Result.Success(Unit)
    }

    private fun createViewModel(): HistoryDetailViewModel {
        return HistoryDetailViewModel(
            getHistoryDetailUseCase = getHistoryDetailUseCase,
            deleteHistoryUseCase = deleteHistoryUseCase,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun `initial state has no entry`() = runTest {
        // Given - no preconditions

        // When
        viewModel = createViewModel()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.entry)
            assertFalse(state.isLoading)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadEntry loads entry successfully`() = runTest {
        // Given
        val entryId = "test-entry-id"
        viewModel = createViewModel()

        // When
        viewModel.processIntent(HistoryDetailContract.Intent.LoadEntry(entryId))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.entry)
            assertEquals(testEntry, state.entry)
            assertFalse(state.isLoading)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { getHistoryDetailUseCase(entryId) }
    }

    @Test
    fun `LoadEntry shows loading state then loaded state`() = runTest {
        // Given
        viewModel = createViewModel()

        // When & Then
        viewModel.state.test {
            skipItems(1) // Skip initial state

            viewModel.processIntent(HistoryDetailContract.Intent.LoadEntry("test-entry-id"))

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            advanceUntilIdle()

            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertNotNull(loadedState.entry)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadEntry shows error on failure`() = runTest {
        // Given
        val expectedError = "Entry not found"
        coEvery { getHistoryDetailUseCase(any()) } returns Result.Error(
            RuntimeException("Not found"),
            expectedError
        )
        viewModel = createViewModel()

        // When
        viewModel.processIntent(HistoryDetailContract.Intent.LoadEntry("nonexistent"))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.entry)
            assertFalse(state.isLoading)
            assertNotNull(state.error)
            assertEquals(expectedError, state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadEntry uses fallback error message when message is null`() = runTest {
        // Given
        val expectedFallback = "Failed to load entry"
        coEvery { getHistoryDetailUseCase(any()) } returns Result.Error(
            RuntimeException("Not found"),
            null
        )
        viewModel = createViewModel()

        // When
        viewModel.processIntent(HistoryDetailContract.Intent.LoadEntry("nonexistent"))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedFallback, state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DeleteEntry deletes and emits combined effect`() = runTest {
        // Given
        viewModel = createViewModel()
        viewModel.processIntent(HistoryDetailContract.Intent.LoadEntry("test-entry-id"))
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(HistoryDetailContract.Intent.DeleteEntry)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is HistoryDetailContract.Effect.DeleteSuccessAndNavigateBack)
            assertEquals(
                HistoryDetailContract.ToastMessage.EntryDeleted,
                (effect as HistoryDetailContract.Effect.DeleteSuccessAndNavigateBack).message
            )
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { deleteHistoryUseCase("test-entry-id") }
    }

    @Test
    fun `DeleteEntry does nothing when no entry loaded`() = runTest {
        // Given
        viewModel = createViewModel()
        // No entry loaded

        // When
        viewModel.processIntent(HistoryDetailContract.Intent.DeleteEntry)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { deleteHistoryUseCase(any()) }
    }

    @Test
    fun `DeleteEntry shows error on failure`() = runTest {
        // Given
        val expectedError = "Failed to delete entry"
        coEvery { deleteHistoryUseCase(any()) } returns Result.Error(
            RuntimeException("Delete failed"),
            expectedError
        )
        viewModel = createViewModel()
        viewModel.processIntent(HistoryDetailContract.Intent.LoadEntry("test-entry-id"))
        advanceUntilIdle()

        // When
        viewModel.processIntent(HistoryDetailContract.Intent.DeleteEntry)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.error)
            assertEquals(expectedError, state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NavigateBack emits NavigateBack effect`() = runTest {
        // Given
        viewModel = createViewModel()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(HistoryDetailContract.Intent.NavigateBack)

            val effect = awaitItem()
            assertTrue(effect is HistoryDetailContract.Effect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `entry contains correct lottery numbers`() = runTest {
        // Given
        val expectedMainNumbers = listOf(5, 12, 23, 45, 67)
        val expectedBonusNumbers = listOf(15)
        val expectedTypeName = "Powerball"
        viewModel = createViewModel()

        // When
        viewModel.processIntent(HistoryDetailContract.Intent.LoadEntry("test-entry-id"))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.entry)
            assertEquals(expectedMainNumbers, state.entry?.mainNumbers)
            assertEquals(expectedBonusNumbers, state.entry?.bonusNumbers)
            assertEquals(expectedTypeName, state.entry?.lotteryType?.displayName)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
