package com.aaltix.lotto.feature.history.presentation.list

import androidx.lifecycle.viewModelScope
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.mvi.BaseViewModel
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.usecase.ClearHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.DeleteHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.GetHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.GetLotteryTypesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for the History List screen implementing MVI pattern.
 */
class HistoryListViewModel(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val getLotteryTypesUseCase: GetLotteryTypesUseCase,
    private val dispatcherProvider: DispatcherProvider
) : BaseViewModel<HistoryListContract.State, HistoryListContract.Intent, HistoryListContract.Effect>(
    HistoryListContract.State()
) {

    private var historyJob: Job? = null

    init {
        processIntent(HistoryListContract.Intent.LoadHistory)
    }

    override fun handleIntent(intent: HistoryListContract.Intent) {
        when (intent) {
            is HistoryListContract.Intent.LoadHistory -> loadHistory()
            is HistoryListContract.Intent.FilterByType -> filterByType(intent.type)
            is HistoryListContract.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is HistoryListContract.Intent.ShowClearAllDialog -> showClearAllDialog()
            is HistoryListContract.Intent.DismissClearAllDialog -> dismissClearAllDialog()
            is HistoryListContract.Intent.ConfirmClearAll -> confirmClearAll()
            is HistoryListContract.Intent.ViewDetail -> viewDetail(intent.entryId)
        }
    }

    private fun loadHistory() {
        loadAvailableFilters()
        observeHistory()
    }

    private fun filterByType(type: LotteryType?) {
        setState { copy(selectedFilter = type) }
        observeHistory()
    }

    private fun deleteEntry(entryId: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = deleteHistoryUseCase(entryId)) {
                is Result.Success -> {
                    sendEffect(
                        HistoryListContract.Effect.ShowToast(
                            HistoryListContract.ToastMessage.EntryDeleted
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

    private fun showClearAllDialog() {
        setState { copy(showClearConfirmDialog = true) }
    }

    private fun dismissClearAllDialog() {
        setState { copy(showClearConfirmDialog = false) }
    }

    private fun confirmClearAll() {
        setState { copy(showClearConfirmDialog = false) }

        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = clearHistoryUseCase()) {
                is Result.Success -> {
                    sendEffect(
                        HistoryListContract.Effect.ShowToast(
                            HistoryListContract.ToastMessage.HistoryCleared
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

    private fun viewDetail(entryId: String) {
        sendEffect(HistoryListContract.Effect.NavigateToDetail(entryId))
    }

    private fun loadAvailableFilters() {
        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = getLotteryTypesUseCase()) {
                is Result.Success -> {
                    setState {
                        val updatedSelected = selectedFilter?.let { current ->
                            result.data.find { it.id == current.id }
                        }
                        copy(
                            availableFilters = result.data,
                            selectedFilter = updatedSelected
                        )
                    }
                }
                is Result.Error -> {
                    setState { copy(error = result.message) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun observeHistory() {
        historyJob?.cancel()
        setState { copy(isLoading = true) }
        historyJob = viewModelScope.launch(dispatcherProvider.io) {
            val selectedFilterId = currentState.selectedFilter?.id
            val flow = if (selectedFilterId != null) {
                getHistoryUseCase.byType(selectedFilterId)
            } else {
                getHistoryUseCase()
            }

            flow.collectLatest { entries ->
                setState {
                    copy(
                        historyEntries = entries,
                        isLoading = false,
                        isEmpty = entries.isEmpty()
                    )
                }
            }
        }
    }
}
