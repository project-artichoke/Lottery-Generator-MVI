package com.aaltix.lotto.feature.settings.presentation.addedit

import app.cash.turbine.test
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.feature.settings.util.MainDispatcherRule
import com.aaltix.lotto.feature.settings.util.TestDispatcherProvider
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.usecase.GetCustomLotteryTypeByIdUseCase
import com.aaltix.lotto.core.domain.usecase.SaveCustomLotteryTypeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
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
class AddEditLotteryTypeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AddEditLotteryTypeViewModel
    private lateinit var getCustomLotteryTypeByIdUseCase: GetCustomLotteryTypeByIdUseCase
    private lateinit var saveCustomLotteryTypeUseCase: SaveCustomLotteryTypeUseCase
    private lateinit var dispatcherProvider: DispatcherProvider

    private val testCustomType = LotteryType(
        id = "custom_pick3",
        name = "pick_3",
        displayName = "Pick 3",
        mainNumberCount = 3,
        mainNumberMax = 9,
        bonusNumberCount = 0,
        bonusNumberMax = 0,
        isCustom = true
    )

    @Before
    fun setup() {
        getCustomLotteryTypeByIdUseCase = mockk()
        saveCustomLotteryTypeUseCase = mockk()
        dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher)

        coEvery { getCustomLotteryTypeByIdUseCase(any()) } returns Result.Success(testCustomType)
        coEvery { saveCustomLotteryTypeUseCase(any()) } returns Result.Success(testCustomType)
    }

    private fun createViewModel(): AddEditLotteryTypeViewModel {
        return AddEditLotteryTypeViewModel(
            getCustomLotteryTypeByIdUseCase = getCustomLotteryTypeByIdUseCase,
            saveCustomLotteryTypeUseCase = saveCustomLotteryTypeUseCase,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun `initial state has default values for add mode`() = runTest {
        // Given - no preconditions needed for initial state test

        // When
        viewModel = createViewModel()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.typeId)
            assertEquals("", state.name)
            assertEquals(5, state.mainNumberCount)
            assertEquals(50, state.mainNumberMax)
            assertEquals(0, state.bonusNumberCount)
            assertFalse(state.isEditMode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadType loads existing type for edit mode`() = runTest {
        // Given
        val typeId = "custom_pick3"
        viewModel = createViewModel()

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.LoadType(typeId))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(typeId, state.typeId)
            assertEquals("Pick 3", state.name)
            assertEquals(3, state.mainNumberCount)
            assertEquals(9, state.mainNumberMax)
            assertEquals(0, state.bonusNumberCount)
            assertTrue(state.isEditMode)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadType shows error when type not found`() = runTest {
        // Given
        val expectedError = "Type not found"
        coEvery { getCustomLotteryTypeByIdUseCase(any()) } returns Result.Success(null)
        viewModel = createViewModel()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(AddEditLotteryTypeContract.Intent.LoadType("nonexistent"))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is AddEditLotteryTypeContract.Effect.ShowError)
            assertEquals(expectedError, (effect as AddEditLotteryTypeContract.Effect.ShowError).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UpdateName updates name and clears error`() = runTest {
        // Given
        val newName = "My Lottery"
        viewModel = createViewModel()

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateName(newName))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(newName, state.name)
            assertNull(state.nameError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UpdateName shows error for blank name`() = runTest {
        // Given
        val expectedError = "Name is required"
        viewModel = createViewModel()

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateName(""))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.name)
            assertNotNull(state.nameError)
            assertEquals(expectedError, state.nameError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UpdateMainNumberCount updates count`() = runTest {
        // Given
        val expectedCount = 6
        viewModel = createViewModel()

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberCount(expectedCount))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedCount, state.mainNumberCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UpdateMainNumberCount adjusts max if count exceeds max`() = runTest {
        // Given
        val newCount = 15
        viewModel = createViewModel()
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberMax(10))

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberCount(newCount))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(newCount, state.mainNumberCount)
            assertEquals(newCount, state.mainNumberMax) // Max should be adjusted
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UpdateMainNumberMax updates max`() = runTest {
        // Given
        val expectedMax = 99
        viewModel = createViewModel()

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberMax(expectedMax))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedMax, state.mainNumberMax)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UpdateBonusNumberCount updates bonus count and enables bonus`() = runTest {
        // Given
        val expectedCount = 2
        viewModel = createViewModel()

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateBonusNumberCount(expectedCount))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedCount, state.bonusNumberCount)
            assertTrue(state.hasBonusNumbers)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UpdateBonusNumberMax updates bonus max`() = runTest {
        // Given
        val expectedMax = 30
        viewModel = createViewModel()
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateBonusNumberCount(1))

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateBonusNumberMax(expectedMax))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedMax, state.bonusNumberMax)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Save creates new type and emits SaveSuccess`() = runTest {
        // Given
        val expectedName = "New Lottery"
        viewModel = createViewModel()
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateName(expectedName))
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberCount(5))
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberMax(50))

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(AddEditLotteryTypeContract.Intent.Save)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is AddEditLotteryTypeContract.Effect.SaveSuccess)
            cancelAndIgnoreRemainingEvents()
        }

        val typeSlot = slot<LotteryType>()
        coVerify { saveCustomLotteryTypeUseCase(capture(typeSlot)) }

        val savedType = typeSlot.captured
        assertEquals(expectedName, savedType.displayName)
        assertEquals("new_lottery", savedType.name)
        assertEquals(5, savedType.mainNumberCount)
        assertEquals(50, savedType.mainNumberMax)
        assertTrue(savedType.isCustom)
    }

    @Test
    fun `Save does not save when name is blank`() = runTest {
        // Given
        viewModel = createViewModel()
        // Don't set name - leave blank

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.Save)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.nameError)
            assertFalse(state.isSaving)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { saveCustomLotteryTypeUseCase(any()) }
    }

    @Test
    fun `Save shows error on failure`() = runTest {
        // Given
        val expectedError = "Failed to save"
        coEvery { saveCustomLotteryTypeUseCase(any()) } returns Result.Error(
            RuntimeException("Save failed"),
            expectedError
        )
        viewModel = createViewModel()
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateName("Test"))

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(AddEditLotteryTypeContract.Intent.Save)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is AddEditLotteryTypeContract.Effect.ShowError)
            assertEquals(expectedError, (effect as AddEditLotteryTypeContract.Effect.ShowError).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NavigateBack emits NavigateBack effect`() = runTest {
        // Given
        viewModel = createViewModel()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(AddEditLotteryTypeContract.Intent.NavigateBack)

            val effect = awaitItem()
            assertTrue(effect is AddEditLotteryTypeContract.Effect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `State isValid returns true when name is not blank`() = runTest {
        // Given
        viewModel = createViewModel()

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateName("Valid Name"))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isValid)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `State isValid returns false when name is blank`() = runTest {
        // Given - no name set

        // When
        viewModel = createViewModel()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isValid)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `State previewMainNumbers shows correct format`() = runTest {
        // Given
        val expectedPreview = "6 numbers from 1-49"
        viewModel = createViewModel()

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberCount(6))
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberMax(49))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedPreview, state.previewMainNumbers)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `State previewBonusNumbers shows correct format when bonus enabled`() = runTest {
        // Given
        val expectedPreview = "1 bonus from 1-26"
        viewModel = createViewModel()

        // When
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateBonusNumberCount(1))
        viewModel.processIntent(AddEditLotteryTypeContract.Intent.UpdateBonusNumberMax(26))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedPreview, state.previewBonusNumbers)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `State previewBonusNumbers shows no bonus when disabled`() = runTest {
        // Given
        val expectedPreview = "No bonus numbers"

        // When
        viewModel = createViewModel()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedPreview, state.previewBonusNumbers)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
