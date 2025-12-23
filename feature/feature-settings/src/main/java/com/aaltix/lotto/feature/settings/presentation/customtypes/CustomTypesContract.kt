package com.aaltix.lotto.feature.settings.presentation.customtypes

import com.aaltix.lotto.core.domain.model.LotteryType

/**
 * MVI Contract for the Custom Lottery Types list screen.
 */
object CustomTypesContract {

    /**
     * UI State for the Custom Types list screen.
     */
    data class State(
        val customTypes: List<LotteryType> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    /**
     * User intents/actions for the Custom Types list screen.
     */
    sealed class Intent {
        data object LoadCustomTypes : Intent()
        data object AddCustomType : Intent()
        data class EditCustomType(val typeId: String) : Intent()
        data class DeleteCustomType(val typeId: String) : Intent()
        data object NavigateBack : Intent()
    }

    /**
     * One-time side effects for the Custom Types list screen.
     */
    sealed class Effect {
        data object NavigateToAddType : Effect()
        data class NavigateToEditType(val typeId: String) : Effect()
        data object NavigateBack : Effect()
        data class ShowDeleteSuccess(val typeName: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
