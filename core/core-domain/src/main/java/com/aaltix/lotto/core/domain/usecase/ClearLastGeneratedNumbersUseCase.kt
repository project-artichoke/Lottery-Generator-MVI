package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.domain.repository.UserPreferencesRepository

/**
 * Use case for clearing the last generated numbers from preferences.
 * Typically used when switching lottery types.
 */
class ClearLastGeneratedNumbersUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        userPreferencesRepository.clearLastGeneratedNumbers()
    }
}
