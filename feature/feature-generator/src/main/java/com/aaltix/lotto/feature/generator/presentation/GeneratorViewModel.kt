package com.aaltix.lotto.feature.generator.presentation

import androidx.lifecycle.viewModelScope
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.mvi.BaseViewModel
import com.aaltix.lotto.core.common.result.Result
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for the Generator screen implementing MVI pattern.
 */
class GeneratorViewModel(
    private val generateNumbersUseCase: GenerateNumbersUseCase,
    private val getLotteryTypesUseCase: GetLotteryTypesUseCase,
    private val getHistoryDetailUseCase: GetHistoryDetailUseCase,
    private val getSelectedLotteryTypeIdUseCase: GetSelectedLotteryTypeIdUseCase,
    private val saveSelectedLotteryTypeIdUseCase: SaveSelectedLotteryTypeIdUseCase,
    private val getLastGeneratedNumbersIdUseCase: GetLastGeneratedNumbersIdUseCase,
    private val saveLastGeneratedNumbersIdUseCase: SaveLastGeneratedNumbersIdUseCase,
    private val clearLastGeneratedNumbersUseCase: ClearLastGeneratedNumbersUseCase,
    private val checkConfettiShownUseCase: CheckConfettiShownUseCase,
    private val markConfettiShownUseCase: MarkConfettiShownUseCase,
    private val dispatcherProvider: DispatcherProvider
) : BaseViewModel<GeneratorContract.State, GeneratorContract.Intent, GeneratorContract.Effect>(
    GeneratorContract.State()
) {

    init {
        processIntent(GeneratorContract.Intent.LoadLotteryTypes)
    }

    override fun handleIntent(intent: GeneratorContract.Intent) {
        when (intent) {
            is GeneratorContract.Intent.LoadLotteryTypes -> loadLotteryTypes()
            is GeneratorContract.Intent.SelectLotteryType -> selectLotteryType(intent.type)
            is GeneratorContract.Intent.GenerateNumbers -> generateNumbers()
            is GeneratorContract.Intent.AnimationComplete -> onAnimationComplete()
            is GeneratorContract.Intent.ConfettiComplete -> onConfettiComplete()
            is GeneratorContract.Intent.DismissError -> dismissError()
            is GeneratorContract.Intent.NavigateToHistory -> navigateToHistory()
        }
    }

    private fun loadLotteryTypes() {
        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = getLotteryTypesUseCase()) {
                is Result.Success -> {
                    val types = result.data
                    val savedTypeId = getSelectedLotteryTypeIdUseCase().first()
                    val selectedType = types.find { it.id == savedTypeId }
                        ?: types.firstOrNull()
                        ?: currentState.selectedLotteryType

                    setState {
                        copy(
                            availableLotteryTypes = types,
                            selectedLotteryType = selectedType
                        )
                    }

                    // Load last generated numbers for this lottery type
                    loadLastGeneratedNumbers()
                }
                is Result.Error -> {
                    setState { copy(error = result.message) }
                }
                is Result.Loading -> { /* Handled by isLoading */ }
            }
        }
    }

    private fun loadLastGeneratedNumbers() {
        viewModelScope.launch(dispatcherProvider.io) {
            val lastNumbersId = getLastGeneratedNumbersIdUseCase().first()
            if (lastNumbersId != null) {
                when (val result = getHistoryDetailUseCase(lastNumbersId)) {
                    is Result.Success -> {
                        // Only restore if it matches the current lottery type
                        if (result.data.lotteryType.id == currentState.selectedLotteryType.id) {
                            setState { copy(generatedNumbers = result.data) }
                        }
                    }
                    is Result.Error -> {
                        // Silently ignore - numbers may have been deleted from history
                    }
                    is Result.Loading -> Unit
                }
            }
        }
    }

    private fun selectLotteryType(type: LotteryType) {
        setState {
            copy(
                selectedLotteryType = type,
                generatedNumbers = null // Clear previous numbers when type changes
            )
        }
        // Save selection to preferences
        viewModelScope.launch(dispatcherProvider.io) {
            saveSelectedLotteryTypeIdUseCase(type.id)
            clearLastGeneratedNumbersUseCase()
        }
    }

    private fun generateNumbers() {
        // Atomically check loading state and capture lottery type to prevent
        // race conditions with multiple rapid taps
        var capturedType: LotteryType? = null

        setState {
            if (isLoading) {
                this // Return unchanged state if already loading
            } else {
                capturedType = selectedLotteryType
                copy(isLoading = true, error = null)
            }
        }

        // If capturedType is null, we were already loading
        val lotteryType = capturedType ?: return

        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = generateNumbersUseCase(lotteryType)) {
                is Result.Success -> {
                    setState {
                        copy(
                            isLoading = false,
                            generatedNumbers = result.data,
                            isAnimating = true
                        )
                    }
                    // Save generated numbers ID to preferences
                    saveLastGeneratedNumbersIdUseCase(result.data.id)
                    sendEffect(GeneratorContract.Effect.TriggerHapticFeedback)
                }
                is Result.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            error = result.message ?: "Failed to generate numbers"
                        )
                    }
                }
                is Result.Loading -> Unit // Handled by isLoading
            }
        }
    }

    private fun onAnimationComplete() {
        val currentTypeId = currentState.selectedLotteryType.id

        viewModelScope.launch(dispatcherProvider.io) {
            val hasShownConfetti = checkConfettiShownUseCase(currentTypeId)
            val shouldShowConfetti = !hasShownConfetti

            setState {
                copy(
                    isAnimating = false,
                    showConfetti = shouldShowConfetti
                )
            }

            if (shouldShowConfetti) {
                markConfettiShownUseCase(currentTypeId)
            }
        }
    }

    private fun onConfettiComplete() {
        setState { copy(showConfetti = false) }
    }

    private fun dismissError() {
        setState { copy(error = null) }
    }

    private fun navigateToHistory() {
        sendEffect(GeneratorContract.Effect.NavigateToHistory)
    }
}
