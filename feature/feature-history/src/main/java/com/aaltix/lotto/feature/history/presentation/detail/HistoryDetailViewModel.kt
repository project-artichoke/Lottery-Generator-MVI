package com.aaltix.lotto.feature.history.presentation.detail

import androidx.lifecycle.viewModelScope
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.mvi.BaseViewModel
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.usecase.DeleteHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.GetHistoryDetailUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel for the History Detail screen implementing MVI pattern.
 */
class HistoryDetailViewModel(
    private val getHistoryDetailUseCase: GetHistoryDetailUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase,
    private val dispatcherProvider: DispatcherProvider
) : BaseViewModel<HistoryDetailContract.State, HistoryDetailContract.Intent, HistoryDetailContract.Effect>(
    HistoryDetailContract.State()
) {

    override fun handleIntent(intent: HistoryDetailContract.Intent) {
        when (intent) {
            is HistoryDetailContract.Intent.LoadEntry -> loadEntry(intent.entryId)
            is HistoryDetailContract.Intent.DeleteEntry -> deleteEntry()
            is HistoryDetailContract.Intent.NavigateBack -> navigateBack()
        }
    }

    private fun loadEntry(entryId: String) {
        setState { copy(isLoading = true) }

        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = getHistoryDetailUseCase(entryId)) {
                is Result.Success -> {
                    setState {
                        copy(
                            entry = result.data,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load entry"
                        )
                    }
                }
                is Result.Loading -> { /* Already handled */ }
            }
        }
    }

    private fun deleteEntry() {
        val entryId = currentState.entry?.id ?: return

        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = deleteHistoryUseCase(entryId)) {
                is Result.Success -> {
                    // Use combined effect to ensure toast shows before navigation
                    sendEffect(
                        HistoryDetailContract.Effect.DeleteSuccessAndNavigateBack(
                            HistoryDetailContract.ToastMessage.EntryDeleted
                        )
                    )
                }
                is Result.Error -> {
                    setState { copy(error = result.message) }
                }
                is Result.Loading -> { /* Not used */ }
            }
        }
    }

    private fun navigateBack() {
        sendEffect(HistoryDetailContract.Effect.NavigateBack)
    }
}
