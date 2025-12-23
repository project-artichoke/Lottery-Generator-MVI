package com.aaltix.lotto.feature.generator

import app.cash.turbine.test
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.usecase.CheckConfettiShownUseCase
import com.aaltix.lotto.core.domain.usecase.ClearLastGeneratedNumbersUseCase
import com.aaltix.lotto.core.domain.usecase.GenerateNumbersUseCase
import com.aaltix.lotto.core.domain.usecase.GetHistoryDetailUseCase
import com.aaltix.lotto.core.domain.usecase.GetLastGeneratedNumbersIdUseCase
import com.aaltix.lotto.core.domain.usecase.GetLotteryTypesUseCase
import com.aaltix.lotto.core.domain.usecase.GetSelectedLotteryTypeIdUseCase
import com.aaltix.lotto.core.domain.usecase.MarkConfettiShownUseCase
import com.aaltix.lotto.core.domain.usecase.SaveLastGeneratedNumbersIdUseCase
import com.aaltix.lotto.core.domain.usecase.SaveSelectedLotteryTypeIdUseCase
import com.aaltix.lotto.feature.generator.presentation.GeneratorContract
import com.aaltix.lotto.feature.generator.presentation.GeneratorViewModel
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GeneratorViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: GeneratorViewModel
    private lateinit var getLotteryTypesUseCase: GetLotteryTypesUseCase
    private lateinit var generateNumbersUseCase: GenerateNumbersUseCase
    private lateinit var getHistoryDetailUseCase: GetHistoryDetailUseCase
    private lateinit var getSelectedLotteryTypeIdUseCase: GetSelectedLotteryTypeIdUseCase
    private lateinit var saveSelectedLotteryTypeIdUseCase: SaveSelectedLotteryTypeIdUseCase
    private lateinit var getLastGeneratedNumbersIdUseCase: GetLastGeneratedNumbersIdUseCase
    private lateinit var saveLastGeneratedNumbersIdUseCase: SaveLastGeneratedNumbersIdUseCase
    private lateinit var clearLastGeneratedNumbersUseCase: ClearLastGeneratedNumbersUseCase
    private lateinit var checkConfettiShownUseCase: CheckConfettiShownUseCase
    private lateinit var markConfettiShownUseCase: MarkConfettiShownUseCase
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

    private val testGeneratedNumbers = GeneratedNumbers(
        id = "test-id",
        lotteryType = testLotteryTypes[0],
        mainNumbers = listOf(1, 2, 3, 4, 5),
        bonusNumbers = listOf(10),
        timestamp = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getLotteryTypesUseCase = mockk()
        generateNumbersUseCase = mockk()
        getHistoryDetailUseCase = mockk()
        getSelectedLotteryTypeIdUseCase = mockk()
        saveSelectedLotteryTypeIdUseCase = mockk()
        getLastGeneratedNumbersIdUseCase = mockk()
        saveLastGeneratedNumbersIdUseCase = mockk()
        clearLastGeneratedNumbersUseCase = mockk()
        checkConfettiShownUseCase = mockk()
        markConfettiShownUseCase = mockk()
        dispatcherProvider = object : DispatcherProvider {
            override val main: CoroutineDispatcher = testDispatcher
            override val io: CoroutineDispatcher = testDispatcher
            override val default: CoroutineDispatcher = testDispatcher
            override val unconfined: CoroutineDispatcher = testDispatcher
        }

        // Default mock behaviors
        coEvery { getLotteryTypesUseCase() } returns Result.Success(testLotteryTypes)
        coEvery { generateNumbersUseCase(any()) } returns Result.Success(testGeneratedNumbers)
        coEvery { getSelectedLotteryTypeIdUseCase() } returns flowOf(null)
        coEvery { getLastGeneratedNumbersIdUseCase() } returns flowOf(null)
        coEvery { checkConfettiShownUseCase(any()) } returns false
        coJustRun { saveSelectedLotteryTypeIdUseCase(any()) }
        coJustRun { saveLastGeneratedNumbersIdUseCase(any()) }
        coJustRun { clearLastGeneratedNumbersUseCase() }
        coJustRun { markConfettiShownUseCase(any()) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GeneratorViewModel {
        return GeneratorViewModel(
            generateNumbersUseCase = generateNumbersUseCase,
            getLotteryTypesUseCase = getLotteryTypesUseCase,
            getHistoryDetailUseCase = getHistoryDetailUseCase,
            getSelectedLotteryTypeIdUseCase = getSelectedLotteryTypeIdUseCase,
            saveSelectedLotteryTypeIdUseCase = saveSelectedLotteryTypeIdUseCase,
            getLastGeneratedNumbersIdUseCase = getLastGeneratedNumbersIdUseCase,
            saveLastGeneratedNumbersIdUseCase = saveLastGeneratedNumbersIdUseCase,
            clearLastGeneratedNumbersUseCase = clearLastGeneratedNumbersUseCase,
            checkConfettiShownUseCase = checkConfettiShownUseCase,
            markConfettiShownUseCase = markConfettiShownUseCase,
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
            assertEquals(expectedTypes, state.availableLotteryTypes)
            assertEquals(expectedTypes[0], state.selectedLotteryType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SelectLotteryType updates selected type`() = runTest {
        // Given
        val expectedType = testLotteryTypes[1]
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.processIntent(GeneratorContract.Intent.SelectLotteryType(expectedType))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedType, state.selectedLotteryType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GenerateNumbers shows loading then results`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.state.test {
            skipItems(1) // Skip initial state

            viewModel.processIntent(GeneratorContract.Intent.GenerateNumbers)

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            advanceUntilIdle()

            val resultState = awaitItem()
            assertFalse(resultState.isLoading)
            assertTrue(resultState.isAnimating)
            assertNotNull(resultState.generatedNumbers)
            assertEquals(testGeneratedNumbers, resultState.generatedNumbers)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GenerateNumbers calls use case with selected lottery type`() = runTest {
        // Given
        val expectedType = testLotteryTypes[0]
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.processIntent(GeneratorContract.Intent.GenerateNumbers)
        advanceUntilIdle()

        // Then
        coVerify { generateNumbersUseCase(expectedType) }
    }

    @Test
    fun `GenerateNumbers shows error on failure`() = runTest {
        // Given
        val expectedError = "Failed to generate"
        coEvery { generateNumbersUseCase(any()) } returns Result.Error(
            RuntimeException("Test error"),
            expectedError
        )
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.processIntent(GeneratorContract.Intent.GenerateNumbers)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.error)
            assertEquals(expectedError, state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AnimationComplete triggers confetti on first generation for game type`() = runTest {
        // Given
        coEvery { checkConfettiShownUseCase(any()) } returns false
        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.processIntent(GeneratorContract.Intent.GenerateNumbers)
        advanceUntilIdle()

        // When
        viewModel.processIntent(GeneratorContract.Intent.AnimationComplete)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isAnimating)
            assertTrue(state.showConfetti)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { markConfettiShownUseCase(testLotteryTypes[0].id) }
    }

    @Test
    fun `AnimationComplete does not trigger confetti on subsequent generations`() = runTest {
        // Given
        coEvery { checkConfettiShownUseCase(any()) } returns true
        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.processIntent(GeneratorContract.Intent.GenerateNumbers)
        advanceUntilIdle()

        // When
        viewModel.processIntent(GeneratorContract.Intent.AnimationComplete)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isAnimating)
            assertFalse(state.showConfetti)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { markConfettiShownUseCase(any()) }
    }

    @Test
    fun `AnimationComplete triggers confetti when switching to new game type`() = runTest {
        // Given - first type already shown, second type not shown
        coEvery { checkConfettiShownUseCase(testLotteryTypes[0].id) } returns true
        coEvery { checkConfettiShownUseCase(testLotteryTypes[1].id) } returns false
        viewModel = createViewModel()
        advanceUntilIdle()

        // Switch to second type and generate
        viewModel.processIntent(GeneratorContract.Intent.SelectLotteryType(testLotteryTypes[1]))
        advanceUntilIdle()

        val numbersForType2 = testGeneratedNumbers.copy(lotteryType = testLotteryTypes[1])
        coEvery { generateNumbersUseCase(testLotteryTypes[1]) } returns Result.Success(numbersForType2)

        viewModel.processIntent(GeneratorContract.Intent.GenerateNumbers)
        advanceUntilIdle()

        // When
        viewModel.processIntent(GeneratorContract.Intent.AnimationComplete)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showConfetti)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { markConfettiShownUseCase(testLotteryTypes[1].id) }
    }

    @Test
    fun `ConfettiComplete hides confetti`() = runTest {
        // Given
        coEvery { checkConfettiShownUseCase(any()) } returns false
        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.processIntent(GeneratorContract.Intent.GenerateNumbers)
        advanceUntilIdle()
        viewModel.processIntent(GeneratorContract.Intent.AnimationComplete)
        advanceUntilIdle()

        // When
        viewModel.processIntent(GeneratorContract.Intent.ConfettiComplete)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.showConfetti)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DismissError clears error`() = runTest {
        // Given
        coEvery { generateNumbersUseCase(any()) } returns Result.Error(
            RuntimeException("Test error"),
            "Failed"
        )
        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.processIntent(GeneratorContract.Intent.GenerateNumbers)
        advanceUntilIdle()

        // Verify error is set
        viewModel.state.test {
            assertNotNull(awaitItem().error)
            cancelAndIgnoreRemainingEvents()
        }

        // When
        viewModel.processIntent(GeneratorContract.Intent.DismissError)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NavigateToHistory emits navigation effect`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(GeneratorContract.Intent.NavigateToHistory)

            val effect = awaitItem()
            assertTrue(effect is GeneratorContract.Effect.NavigateToHistory)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading lottery types error sets error state`() = runTest {
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
            assertTrue(state.availableLotteryTypes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GenerateNumbers saves numbers id via use case`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.processIntent(GeneratorContract.Intent.GenerateNumbers)
        advanceUntilIdle()

        // Then
        coVerify { saveLastGeneratedNumbersIdUseCase(testGeneratedNumbers.id) }
    }

    @Test
    fun `SelectLotteryType saves type id via use case`() = runTest {
        // Given
        val expectedType = testLotteryTypes[1]
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.processIntent(GeneratorContract.Intent.SelectLotteryType(expectedType))
        advanceUntilIdle()

        // Then
        coVerify { saveSelectedLotteryTypeIdUseCase(expectedType.id) }
    }

    @Test
    fun `SelectLotteryType clears last generated numbers via use case`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.processIntent(GeneratorContract.Intent.SelectLotteryType(testLotteryTypes[1]))
        advanceUntilIdle()

        // Then
        coVerify { clearLastGeneratedNumbersUseCase() }
    }

    @Test
    fun `restores previously selected lottery type from preferences`() = runTest {
        // Given
        val savedTypeId = testLotteryTypes[1].id
        coEvery { getSelectedLotteryTypeIdUseCase() } returns flowOf(savedTypeId)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(testLotteryTypes[1], state.selectedLotteryType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `restores last generated numbers from preferences`() = runTest {
        // Given
        val lastNumbersId = testGeneratedNumbers.id
        coEvery { getLastGeneratedNumbersIdUseCase() } returns flowOf(lastNumbersId)
        coEvery { getHistoryDetailUseCase(lastNumbersId) } returns Result.Success(testGeneratedNumbers)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(testGeneratedNumbers, state.generatedNumbers)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `does not restore last generated numbers if lottery type differs`() = runTest {
        // Given - saved numbers are for a different lottery type
        val lastNumbersId = testGeneratedNumbers.id
        val numbersForDifferentType = testGeneratedNumbers.copy(
            lotteryType = testLotteryTypes[1]
        )
        coEvery { getLastGeneratedNumbersIdUseCase() } returns flowOf(lastNumbersId)
        coEvery { getHistoryDetailUseCase(lastNumbersId) } returns Result.Success(numbersForDifferentType)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.generatedNumbers)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
