package com.aaltix.lotto.feature.settings.presentation

import app.cash.turbine.test
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.feature.settings.util.MainDispatcherRule
import com.aaltix.lotto.feature.settings.util.TestDispatcherProvider
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.usecase.GetCustomLotteryTypesCountUseCase
import com.aaltix.lotto.core.domain.usecase.GetLotteryTypesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var getLotteryTypesUseCase: GetLotteryTypesUseCase
    private lateinit var getCustomLotteryTypesCountUseCase: GetCustomLotteryTypesCountUseCase
    private lateinit var dispatcherProvider: DispatcherProvider

    private val testLotteryTypes = listOf(
        LotteryType(
            id = "powerball",
            name = "Powerball",
            displayName = "Powerball",
            mainNumberCount = 5,
            mainNumberMax = 69,
            bonusNumberCount = 1,
            bonusNumberMax = 26
        ),
        LotteryType(
            id = "mega_millions",
            name = "Mega Millions",
            displayName = "Mega Millions",
            mainNumberCount = 5,
            mainNumberMax = 70,
            bonusNumberCount = 1,
            bonusNumberMax = 25
        )
    )

    @Before
    fun setup() {
        getLotteryTypesUseCase = mockk()
        getCustomLotteryTypesCountUseCase = mockk()
        dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher)

        coEvery { getLotteryTypesUseCase() } returns Result.Success(testLotteryTypes)
        every { getCustomLotteryTypesCountUseCase() } returns flowOf(0)
    }

    private fun createViewModel(): SettingsViewModel {
        return SettingsViewModel(
            getLotteryTypesUseCase = getLotteryTypesUseCase,
            getCustomLotteryTypesCountUseCase = getCustomLotteryTypesCountUseCase,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun `initial state loads lottery types`() = runTest {
        // Given
        val expectedTypes = testLotteryTypes

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedTypes, state.lotteryTypes)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial state observes custom types count`() = runTest {
        // Given
        val expectedCount = 3
        every { getCustomLotteryTypesCountUseCase() } returns flowOf(expectedCount)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedCount, state.customTypesCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadSettings shows error on failure`() = runTest {
        // Given
        val expectedError = "Failed to load lottery types"
        coEvery { getLotteryTypesUseCase() } returns Result.Error(
            RuntimeException("Network error"),
            expectedError
        )

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.error)
            assertEquals(expectedError, state.error)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ViewLotteryTypeInfo emits ShowLotteryTypeInfo effect`() = runTest {
        // Given
        val expectedType = testLotteryTypes[0]
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(SettingsContract.Intent.ViewLotteryTypeInfo(expectedType))

            val effect = awaitItem()
            assertTrue(effect is SettingsContract.Effect.ShowLotteryTypeInfo)
            assertEquals(expectedType, (effect as SettingsContract.Effect.ShowLotteryTypeInfo).type)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NavigateToCustomTypes emits NavigateToCustomTypes effect`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(SettingsContract.Intent.NavigateToCustomTypes)

            val effect = awaitItem()
            assertTrue(effect is SettingsContract.Effect.NavigateToCustomTypes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `custom types count updates when flow emits new value`() = runTest {
        // Given
        val countFlow = MutableStateFlow(0)
        every { getCustomLotteryTypesCountUseCase() } returns countFlow
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.state.test {
            assertEquals(0, awaitItem().customTypesCount)

            countFlow.value = 5
            assertEquals(5, awaitItem().customTypesCount)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
