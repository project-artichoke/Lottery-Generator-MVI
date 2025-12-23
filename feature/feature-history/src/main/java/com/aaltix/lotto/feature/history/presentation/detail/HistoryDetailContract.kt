package com.aaltix.lotto.feature.history.presentation.detail

import com.aaltix.lotto.core.domain.model.GeneratedNumbers

/**
 * MVI Contract for the History Detail feature.
 */
object HistoryDetailContract {

    /**
     * UI State for the History Detail screen.
     */
    data class State(
        val entry: GeneratedNumbers? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    /**
     * User intents/actions for the History Detail screen.
     */
    sealed class Intent {
        data class LoadEntry(val entryId: String) : Intent()
        data object DeleteEntry : Intent()
        data object NavigateBack : Intent()
    }

    /**
     * One-time side effects for the History Detail screen.
     */
    sealed class Effect {
        data object NavigateBack : Effect()
        data class ShowToast(val message: ToastMessage) : Effect()

        /**
         * Combined effect for delete success: shows toast then navigates back.
         * This ensures proper sequencing - UI can show toast before navigating.
         */
        data class DeleteSuccessAndNavigateBack(val message: ToastMessage) : Effect()
    }

    /**
     * Toast messages for localization support.
     * Actual strings are resolved in the UI layer using string resources.
     */
    enum class ToastMessage {
        EntryDeleted,
        DeleteFailed
    }
}
