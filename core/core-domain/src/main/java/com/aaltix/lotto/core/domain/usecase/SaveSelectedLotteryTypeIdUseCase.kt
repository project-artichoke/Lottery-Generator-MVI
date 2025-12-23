package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.domain.repository.UserPreferencesRepository

/**
 * Use case for saving the selected lottery type ID to preferences.
 */
class SaveSelectedLotteryTypeIdUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(typeId: String) {
        userPreferencesRepository.saveSelectedLotteryTypeId(typeId)
    }
}
