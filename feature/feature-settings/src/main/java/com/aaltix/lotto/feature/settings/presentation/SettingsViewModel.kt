package com.aaltix.lotto.feature.settings.presentation

import androidx.lifecycle.viewModelScope
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.mvi.BaseViewModel
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.usecase.GetCustomLotteryTypesCountUseCase
import com.aaltix.lotto.core.domain.usecase.GetLotteryTypesUseCase
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen implementing MVI pattern.
 */
class SettingsViewModel(
    private val getLotteryTypesUseCase: GetLotteryTypesUseCase,
    private val getCustomLotteryTypesCountUseCase: GetCustomLotteryTypesCountUseCase,
    private val dispatcherProvider: DispatcherProvider
) : BaseViewModel<SettingsContract.State, SettingsContract.Intent, SettingsContract.Effect>(
    SettingsContract.State()
) {

    init {
        processIntent(SettingsContract.Intent.LoadSettings)
        observeCustomTypesCount()
    }

    override fun handleIntent(intent: SettingsContract.Intent) {
        when (intent) {
            is SettingsContract.Intent.LoadSettings -> loadSettings()
            is SettingsContract.Intent.ViewLotteryTypeInfo -> viewLotteryTypeInfo(intent.type)
            is SettingsContract.Intent.NavigateToCustomTypes -> navigateToCustomTypes()
        }
    }

    private fun loadSettings() {
        setState { copy(isLoading = true) }

        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = getLotteryTypesUseCase()) {
                is Result.Success -> {
                    setState {
                        copy(
                            lotteryTypes = result.data,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> { /* Already handled */ }
            }
        }
    }

    private fun observeCustomTypesCount() {
        viewModelScope.launch {
            getCustomLotteryTypesCountUseCase()
                .flowOn(dispatcherProvider.io)
                .collect { count ->
                    setState { copy(customTypesCount = count) }
                }
        }
    }

    private fun viewLotteryTypeInfo(type: LotteryType) {
        sendEffect(SettingsContract.Effect.ShowLotteryTypeInfo(type))
    }

    private fun navigateToCustomTypes() {
        sendEffect(SettingsContract.Effect.NavigateToCustomTypes)
    }
}
