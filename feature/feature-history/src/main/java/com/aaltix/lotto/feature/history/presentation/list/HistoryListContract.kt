package com.aaltix.lotto.feature.history.presentation.list

import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType

/**
 * MVI Contract for the History List feature.
 */
object HistoryListContract {

    /**
     * UI State for the History List screen.
     */
    data class State(
        val historyEntries: List<GeneratedNumbers> = emptyList(),
        val isLoading: Boolean = false,
        val isEmpty: Boolean = false,
        val selectedFilter: LotteryType? = null,
        val availableFilters: List<LotteryType> = emptyList(),
        val showClearConfirmDialog: Boolean = false,
        val error: String? = null
    )

    /**
     * User intents/actions for the History List screen.
     */
    sealed class Intent {
        data object LoadHistory : Intent()
        data class FilterByType(val type: LotteryType?) : Intent()
        data class DeleteEntry(val entryId: String) : Intent()
        data object ShowClearAllDialog : Intent()
        data object DismissClearAllDialog : Intent()
        data object ConfirmClearAll : Intent()
        data class ViewDetail(val entryId: String) : Intent()
    }

    /**
     * One-time side effects for the History List screen.
     */
    sealed class Effect {
        data class NavigateToDetail(val entryId: String) : Effect()
        data class ShowToast(val message: ToastMessage) : Effect()
    }

    enum class ToastMessage {
        EntryDeleted,
        HistoryCleared
    }
}
