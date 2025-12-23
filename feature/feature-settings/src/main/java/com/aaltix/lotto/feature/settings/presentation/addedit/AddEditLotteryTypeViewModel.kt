package com.aaltix.lotto.feature.settings.presentation.addedit

import androidx.lifecycle.viewModelScope
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.mvi.BaseViewModel
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.usecase.GetCustomLotteryTypeByIdUseCase
import com.aaltix.lotto.core.domain.usecase.SaveCustomLotteryTypeUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add/Edit Custom Lottery Type screen.
 */
class AddEditLotteryTypeViewModel(
    private val getCustomLotteryTypeByIdUseCase: GetCustomLotteryTypeByIdUseCase,
    private val saveCustomLotteryTypeUseCase: SaveCustomLotteryTypeUseCase,
    private val dispatcherProvider: DispatcherProvider
) : BaseViewModel<AddEditLotteryTypeContract.State, AddEditLotteryTypeContract.Intent, AddEditLotteryTypeContract.Effect>(
    AddEditLotteryTypeContract.State()
) {

    override fun handleIntent(intent: AddEditLotteryTypeContract.Intent) {
        when (intent) {
            is AddEditLotteryTypeContract.Intent.LoadType -> loadType(intent.typeId)
            is AddEditLotteryTypeContract.Intent.UpdateName -> updateName(intent.name)
            is AddEditLotteryTypeContract.Intent.UpdateMainNumberCount -> updateMainNumberCount(intent.count)
            is AddEditLotteryTypeContract.Intent.UpdateMainNumberMax -> updateMainNumberMax(intent.max)
            is AddEditLotteryTypeContract.Intent.UpdateBonusNumberCount -> updateBonusNumberCount(intent.count)
            is AddEditLotteryTypeContract.Intent.UpdateBonusNumberMax -> updateBonusNumberMax(intent.max)
            is AddEditLotteryTypeContract.Intent.Save -> save()
            is AddEditLotteryTypeContract.Intent.NavigateBack -> navigateBack()
        }
    }

    private fun loadType(typeId: String) {
        setState { copy(isLoading = true, typeId = typeId) }

        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = getCustomLotteryTypeByIdUseCase(typeId)) {
                is Result.Success -> {
                    result.data?.let { type ->
                        setState {
                            copy(
                                isLoading = false,
                                name = type.displayName,
                                mainNumberCount = type.mainNumberCount,
                                mainNumberMax = type.mainNumberMax,
                                bonusNumberCount = type.bonusNumberCount,
                                bonusNumberMax = type.bonusNumberMax
                            )
                        }
                    } ?: run {
                        setState { copy(isLoading = false) }
                        sendEffect(AddEditLotteryTypeContract.Effect.ShowError("Type not found"))
                    }
                }
                is Result.Error -> {
                    setState { copy(isLoading = false) }
                    sendEffect(AddEditLotteryTypeContract.Effect.ShowError(result.message ?: "Unknown error"))
                }
                is Result.Loading -> { /* Already handled */ }
            }
        }
    }

    private fun updateName(name: String) {
        setState { copy(name = name) }
    }

    private fun updateMainNumberCount(count: Int) {
        setState {
            copy(
                mainNumberCount = count,
                // Ensure max is at least equal to count
                mainNumberMax = if (mainNumberMax < count) count else mainNumberMax
            )
        }
    }

    private fun updateMainNumberMax(max: Int) {
        setState { copy(mainNumberMax = max) }
    }

    private fun updateBonusNumberCount(count: Int) {
        setState {
            copy(
                bonusNumberCount = count,
                // Ensure bonus max is at least equal to count when bonus is enabled
                bonusNumberMax = if (count > 0 && bonusNumberMax < count) count else bonusNumberMax
            )
        }
    }

    private fun updateBonusNumberMax(max: Int) {
        setState { copy(bonusNumberMax = max) }
    }

    private fun save() {
        setState { copy(hasAttemptedSave = true) }

        val currentState = state.value

        if (!currentState.isValid) {
            return
        }

        setState { copy(isSaving = true) }

        val lotteryType = LotteryType(
            id = currentState.typeId ?: "",
            name = currentState.name.lowercase().replace(" ", "_"),
            displayName = currentState.name,
            mainNumberCount = currentState.mainNumberCount,
            mainNumberMax = currentState.mainNumberMax,
            bonusNumberCount = currentState.bonusNumberCount,
            bonusNumberMax = if (currentState.bonusNumberCount > 0) currentState.bonusNumberMax else 0,
            isCustom = true
        )

        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = saveCustomLotteryTypeUseCase(lotteryType)) {
                is Result.Success -> {
                    setState { copy(isSaving = false) }
                    sendEffect(AddEditLotteryTypeContract.Effect.SaveSuccess)
                }
                is Result.Error -> {
                    setState { copy(isSaving = false) }
                    sendEffect(AddEditLotteryTypeContract.Effect.ShowError(result.message ?: "Failed to save"))
                }
                is Result.Loading -> { /* Already handled */ }
            }
        }
    }

    private fun navigateBack() {
        sendEffect(AddEditLotteryTypeContract.Effect.NavigateBack)
    }
}
