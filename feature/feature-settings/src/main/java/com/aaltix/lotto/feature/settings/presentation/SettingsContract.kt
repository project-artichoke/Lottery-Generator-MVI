package com.aaltix.lotto.feature.settings.presentation

import com.aaltix.lotto.core.domain.model.LotteryType

/**
 * MVI Contract for the Settings feature.
 */
object SettingsContract {

    /**
     * UI State for the Settings screen.
     */
    data class State(
        val lotteryTypes: List<LotteryType> = emptyList(),
        val customTypesCount: Int = 0,
        val isLoading: Boolean = false,
        val appVersion: String = "1.0.0",
        val error: String? = null
    )

    /**
     * User intents/actions for the Settings screen.
     */
    sealed class Intent {
        data object LoadSettings : Intent()
        data class ViewLotteryTypeInfo(val type: LotteryType) : Intent()
        data object NavigateToCustomTypes : Intent()
    }

    /**
     * One-time side effects for the Settings screen.
     */
    sealed class Effect {
        data class ShowLotteryTypeInfo(val type: LotteryType) : Effect()
        data object NavigateToCustomTypes : Effect()
    }
}
