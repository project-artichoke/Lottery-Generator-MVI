package com.aaltix.lotto.feature.settings.presentation.customtypes

import androidx.lifecycle.viewModelScope
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.mvi.BaseViewModel
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.usecase.DeleteCustomLotteryTypeUseCase
import com.aaltix.lotto.core.domain.usecase.GetCustomLotteryTypesUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Custom Lottery Types list screen.
 */
class CustomTypesViewModel(
    private val getCustomLotteryTypesUseCase: GetCustomLotteryTypesUseCase,
    private val deleteCustomLotteryTypeUseCase: DeleteCustomLotteryTypeUseCase,
    private val dispatcherProvider: DispatcherProvider
) : BaseViewModel<CustomTypesContract.State, CustomTypesContract.Intent, CustomTypesContract.Effect>(
    CustomTypesContract.State()
) {

    init {
        processIntent(CustomTypesContract.Intent.LoadCustomTypes)
    }

    override fun handleIntent(intent: CustomTypesContract.Intent) {
        when (intent) {
            is CustomTypesContract.Intent.LoadCustomTypes -> loadCustomTypes()
            is CustomTypesContract.Intent.AddCustomType -> navigateToAdd()
            is CustomTypesContract.Intent.EditCustomType -> navigateToEdit(intent.typeId)
            is CustomTypesContract.Intent.DeleteCustomType -> deleteCustomType(intent.typeId)
            is CustomTypesContract.Intent.NavigateBack -> navigateBack()
        }
    }

    private fun loadCustomTypes() {
        setState { copy(isLoading = true) }

        viewModelScope.launch {
            getCustomLotteryTypesUseCase()
                .flowOn(dispatcherProvider.io)
                .catch { e ->
                    setState { copy(isLoading = false, error = e.message) }
                }
                .collect { types ->
                    setState { copy(customTypes = types, isLoading = false, error = null) }
                }
        }
    }

    private fun navigateToAdd() {
        sendEffect(CustomTypesContract.Effect.NavigateToAddType)
    }

    private fun navigateToEdit(typeId: String) {
        sendEffect(CustomTypesContract.Effect.NavigateToEditType(typeId))
    }

    private fun deleteCustomType(typeId: String) {
        val typeName = state.value.customTypes.find { it.id == typeId }?.displayName ?: ""

        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = deleteCustomLotteryTypeUseCase(typeId)) {
                is Result.Success -> {
                    sendEffect(CustomTypesContract.Effect.ShowDeleteSuccess(typeName))
                }
                is Result.Error -> {
                    sendEffect(CustomTypesContract.Effect.ShowError(result.message ?: "Failed to delete"))
                }
                is Result.Loading -> { /* Already handled */ }
            }
        }
    }

    private fun navigateBack() {
        sendEffect(CustomTypesContract.Effect.NavigateBack)
    }
}
