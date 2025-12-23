package com.aaltix.lotto.feature.settings.presentation.customtypes

import app.cash.turbine.test
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.feature.settings.util.MainDispatcherRule
import com.aaltix.lotto.feature.settings.util.TestDispatcherProvider
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.usecase.DeleteCustomLotteryTypeUseCase
import com.aaltix.lotto.core.domain.usecase.GetCustomLotteryTypesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CustomTypesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CustomTypesViewModel
    private lateinit var getCustomLotteryTypesUseCase: GetCustomLotteryTypesUseCase
    private lateinit var deleteCustomLotteryTypeUseCase: DeleteCustomLotteryTypeUseCase
    private lateinit var dispatcherProvider: DispatcherProvider

    private val testCustomTypes = listOf(
        LotteryType(
            id = "custom_pick3",
            name = "pick_3",
            displayName = "Pick 3",
            mainNumberCount = 3,
            mainNumberMax = 9,
            bonusNumberCount = 0,
            bonusNumberMax = 0,
            isCustom = true
        ),
        LotteryType(
            id = "custom_pick4",
            name = "pick_4",
            displayName = "Pick 4",
            mainNumberCount = 4,
            mainNumberMax = 9,
            bonusNumberCount = 0,
            bonusNumberMax = 0,
            isCustom = true
        )
    )

    @Before
    fun setup() {
        getCustomLotteryTypesUseCase = mockk()
        deleteCustomLotteryTypeUseCase = mockk()
        dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher)

        every { getCustomLotteryTypesUseCase() } returns flowOf(testCustomTypes)
        coEvery { deleteCustomLotteryTypeUseCase(any()) } returns Result.Success(Unit)
    }

    private fun createViewModel(): CustomTypesViewModel {
        return CustomTypesViewModel(
            getCustomLotteryTypesUseCase = getCustomLotteryTypesUseCase,
            deleteCustomLotteryTypeUseCase = deleteCustomLotteryTypeUseCase,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun `initial state loads custom types`() = runTest {
        // Given
        val expectedTypes = testCustomTypes

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedTypes, state.customTypes)
            assertFalse(state.isLoading)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadCustomTypes shows empty list when no custom types`() = runTest {
        // Given
        every { getCustomLotteryTypesUseCase() } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.customTypes.isEmpty())
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AddCustomType emits NavigateToAddType effect`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(CustomTypesContract.Intent.AddCustomType)

            val effect = awaitItem()
            assertTrue(effect is CustomTypesContract.Effect.NavigateToAddType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `EditCustomType emits NavigateToEditType effect with correct id`() = runTest {
        // Given
        val expectedTypeId = "custom_pick3"
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(CustomTypesContract.Intent.EditCustomType(expectedTypeId))

            val effect = awaitItem()
            assertTrue(effect is CustomTypesContract.Effect.NavigateToEditType)
            assertEquals(expectedTypeId, (effect as CustomTypesContract.Effect.NavigateToEditType).typeId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DeleteCustomType calls use case and shows success`() = runTest {
        // Given
        val typeIdToDelete = "custom_pick3"
        val expectedTypeName = "Pick 3"
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(CustomTypesContract.Intent.DeleteCustomType(typeIdToDelete))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is CustomTypesContract.Effect.ShowDeleteSuccess)
            assertEquals(expectedTypeName, (effect as CustomTypesContract.Effect.ShowDeleteSuccess).typeName)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { deleteCustomLotteryTypeUseCase(typeIdToDelete) }
    }

    @Test
    fun `DeleteCustomType shows error on failure`() = runTest {
        // Given
        val expectedError = "Failed to delete"
        coEvery { deleteCustomLotteryTypeUseCase(any()) } returns Result.Error(
            RuntimeException("Delete failed"),
            expectedError
        )
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(CustomTypesContract.Intent.DeleteCustomType("custom_pick3"))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is CustomTypesContract.Effect.ShowError)
            assertEquals(expectedError, (effect as CustomTypesContract.Effect.ShowError).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NavigateBack emits NavigateBack effect`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.processIntent(CustomTypesContract.Intent.NavigateBack)

            val effect = awaitItem()
            assertTrue(effect is CustomTypesContract.Effect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `custom types list updates when flow emits new values`() = runTest {
        // Given
        val typesFlow = MutableStateFlow(testCustomTypes)
        every { getCustomLotteryTypesUseCase() } returns typesFlow
        viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.state.test {
            assertEquals(2, awaitItem().customTypes.size)

            typesFlow.value = listOf(testCustomTypes[0])
            assertEquals(1, awaitItem().customTypes.size)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
