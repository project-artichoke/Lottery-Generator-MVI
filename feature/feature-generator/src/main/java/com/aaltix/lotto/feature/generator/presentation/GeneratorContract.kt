package com.aaltix.lotto.feature.generator.presentation

import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.model.LotteryTypes

/**
 * MVI Contract for the Generator feature.
 */
object GeneratorContract {

    /**
     * UI State for the Generator screen.
     */
    data class State(
        val selectedLotteryType: LotteryType = LotteryTypes.POWERBALL,
        val availableLotteryTypes: List<LotteryType> = emptyList(),
        val generatedNumbers: GeneratedNumbers? = null,
        val isLoading: Boolean = false,
        val isAnimating: Boolean = false,
        val showConfetti: Boolean = false,
        val error: String? = null
    )

    /**
     * User intents/actions for the Generator screen.
     */
    sealed class Intent {
        data object LoadLotteryTypes : Intent()
        data class SelectLotteryType(val type: LotteryType) : Intent()
        data object GenerateNumbers : Intent()
        data object AnimationComplete : Intent()
        data object ConfettiComplete : Intent()
        data object DismissError : Intent()
        data object NavigateToHistory : Intent()
    }

    /**
     * One-time side effects for the Generator screen.
     */
    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
        data object NavigateToHistory : Effect()
        data object TriggerHapticFeedback : Effect()
    }
}
