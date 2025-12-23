package com.aaltix.lotto.feature.history.presentation.list

import app.cash.turbine.test
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.feature.history.util.MainDispatcherRule
import com.aaltix.lotto.feature.history.util.TestDispatcherProvider
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.model.LotteryTypes
import com.aaltix.lotto.core.domain.usecase.ClearHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.DeleteHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.GetHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.GetLotteryTypesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class HistoryListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var getHistoryUseCase: GetHistoryUseCase
    private lateinit var deleteHistoryUseCase: DeleteHistoryUseCase
    private lateinit var clearHistoryUseCase: ClearHistoryUseCase
    private lateinit var getLotteryTypesUseCase: GetLotteryTypesUseCase

    private val customType = LotteryType(
        id = "custom_pick3",
        name = "Pick 3",
        displayName = "Pick 3",
        mainNumberCount = 3,
        mainNumberMax = 30,
        bonusNumberCount = 0,
        bonusNumberMax = 0,
        isCustom = true
    )

    private val sampleHistory = listOf(
        GeneratedNumbers(
            id = "history-1",
            lotteryType = LotteryTypes.POWERBALL,
            mainNumbers = listOf(1, 2, 3, 4, 5),
            bonusNumbers = listOf(10),
            timestamp = 0L
        ),
        GeneratedNumbers(
            id = "history-2",
            lotteryType = LotteryTypes.POWERBALL,
            mainNumbers = listOf(6, 7, 8, 9, 10),
            bonusNumbers = listOf(20),
            timestamp = 1000L
        )
    )

    @Before
    fun setUp() {
        dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher)
        getHistoryUseCase = mockk()
        deleteHistoryUseCase = mockk()
        clearHistoryUseCase = mockk()
        getLotteryTypesUseCase = mockk()

        every { getHistoryUseCase.invoke() } returns flowOf(sampleHistory)
        every { getHistoryUseCase.byType(any()) } returns flowOf(sampleHistory)
        coEvery { deleteHistoryUseCase.invoke(any()) } returns Result.Success(Unit)
        coEvery { clearHistoryUseCase.invoke() } returns Result.Success(Unit)
        coEvery { getLotteryTypesUseCase.invoke() } returns Result.Success(
            listOf(LotteryTypes.POWERBALL, customType)
        )
    }

    private fun createViewModel(): HistoryListViewModel {
        return HistoryListViewModel(
            getHistoryUseCase = getHistoryUseCase,
            deleteHistoryUseCase = deleteHistoryUseCase,
            clearHistoryUseCase = clearHistoryUseCase,
            getLotteryTypesUseCase = getLotteryTypesUseCase,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun `LoadHistory populates filters and history entries`() = runTest {
        // Given
        val expectedFilterCount = 2
        val expectedHistory = sampleHistory

        // When
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(expectedFilterCount, state.availableFilters.size)
        assertEquals(expectedHistory, state.historyEntries)
        assertFalse(state.isLoading)
        assertFalse(state.isEmpty)
    }

    @Test
    fun `LoadHistory shows empty state when no history`() = runTest {
        // Given
        every { getHistoryUseCase.invoke() } returns flowOf(emptyList())

        // When
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.historyEntries.isEmpty())
        assertTrue(state.isEmpty)
    }

    @Test
    fun `FilterByType updates selected filter and reloads history`() = runTest {
        // Given
        val filterType = LotteryTypes.POWERBALL
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.processIntent(HistoryListContract.Intent.FilterByType(filterType))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(filterType, state.selectedFilter)
    }

    @Test
    fun `FilterByType with null clears filter`() = runTest {
        // Given
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.processIntent(HistoryListContract.Intent.FilterByType(LotteryTypes.POWERBALL))
        advanceUntilIdle()

        // When
        viewModel.processIntent(HistoryListContract.Intent.FilterByType(null))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertNull(state.selectedFilter)
    }

    @Test
    fun `DeleteEntry calls use case and emits success toast`() = runTest {
        // Given
        val entryId = "history-1"
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(HistoryListContract.Intent.DeleteEntry(entryId))
            advanceUntilIdle()

            val effect = awaitItem() as HistoryListContract.Effect.ShowToast
            assertEquals(HistoryListContract.ToastMessage.EntryDeleted, effect.message)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { deleteHistoryUseCase(entryId) }
    }

    @Test
    fun `DeleteEntry shows error on failure`() = runTest {
        // Given
        val expectedError = "Failed to delete"
        coEvery { deleteHistoryUseCase(any()) } returns Result.Error(
            RuntimeException("Delete failed"),
            expectedError
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.processIntent(HistoryListContract.Intent.DeleteEntry("history-1"))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertNotNull(state.error)
        assertEquals(expectedError, state.error)
    }

    @Test
    fun `ShowClearAllDialog sets dialog state to true`() = runTest {
        // Given
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.processIntent(HistoryListContract.Intent.ShowClearAllDialog)

        // Then
        assertTrue(viewModel.state.value.showClearConfirmDialog)
    }

    @Test
    fun `DismissClearAllDialog sets dialog state to false`() = runTest {
        // Given
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.processIntent(HistoryListContract.Intent.ShowClearAllDialog)

        // When
        viewModel.processIntent(HistoryListContract.Intent.DismissClearAllDialog)

        // Then
        assertFalse(viewModel.state.value.showClearConfirmDialog)
    }

    @Test
    fun `ConfirmClearAll calls use case and emits success toast`() = runTest {
        // Given
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(HistoryListContract.Intent.ConfirmClearAll)
            advanceUntilIdle()

            val effect = awaitItem() as HistoryListContract.Effect.ShowToast
            assertEquals(HistoryListContract.ToastMessage.HistoryCleared, effect.message)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { clearHistoryUseCase() }
    }

    @Test
    fun `ConfirmClearAll shows error on failure`() = runTest {
        // Given
        val expectedError = "Failed to clear"
        coEvery { clearHistoryUseCase() } returns Result.Error(
            RuntimeException("Clear failed"),
            expectedError
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.processIntent(HistoryListContract.Intent.ConfirmClearAll)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertNotNull(state.error)
        assertEquals(expectedError, state.error)
    }

    @Test
    fun `ViewDetail emits NavigateToDetail effect`() = runTest {
        // Given
        val entryId = "history-1"
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(HistoryListContract.Intent.ViewDetail(entryId))

            val effect = awaitItem()
            assertTrue(effect is HistoryListContract.Effect.NavigateToDetail)
            assertEquals(entryId, (effect as HistoryListContract.Effect.NavigateToDetail).entryId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
