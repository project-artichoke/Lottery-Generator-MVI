package com.aaltix.lotto.feature.settings.presentation.addedit

/**
 * MVI Contract for the Add/Edit Custom Lottery Type screen.
 */
object AddEditLotteryTypeContract {

    /**
     * UI State for the Add/Edit screen.
     */
    data class State(
        val typeId: String? = null,
        val name: String = "",
        val mainNumberCount: Int = 5,
        val mainNumberMax: Int = 50,
        val bonusNumberCount: Int = 0,
        val bonusNumberMax: Int = 25,
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val nameError: String? = null,
        val error: String? = null
    ) {
        val isEditMode: Boolean get() = typeId != null
        val hasBonusNumbers: Boolean get() = bonusNumberCount > 0
        val isValid: Boolean get() = name.isNotBlank() && nameError == null
        val previewMainNumbers: String get() = "$mainNumberCount numbers from 1-$mainNumberMax"
        val previewBonusNumbers: String get() = if (hasBonusNumbers) {
            "$bonusNumberCount bonus from 1-$bonusNumberMax"
        } else {
            "No bonus numbers"
        }
    }

    /**
     * User intents/actions for the Add/Edit screen.
     */
    sealed class Intent {
        data class LoadType(val typeId: String) : Intent()
        data class UpdateName(val name: String) : Intent()
        data class UpdateMainNumberCount(val count: Int) : Intent()
        data class UpdateMainNumberMax(val max: Int) : Intent()
        data class UpdateBonusNumberCount(val count: Int) : Intent()
        data class UpdateBonusNumberMax(val max: Int) : Intent()
        data object Save : Intent()
        data object NavigateBack : Intent()
    }

    /**
     * One-time side effects for the Add/Edit screen.
     */
    sealed class Effect {
        data object NavigateBack : Effect()
        data object SaveSuccess : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
