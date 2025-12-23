package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting the selected lottery type ID from preferences.
 */
class GetSelectedLotteryTypeIdUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<String?> {
        return userPreferencesRepository.getSelectedLotteryTypeId()
    }
}
