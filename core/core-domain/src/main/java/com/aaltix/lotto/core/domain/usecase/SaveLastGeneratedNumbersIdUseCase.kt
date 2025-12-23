package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.domain.repository.UserPreferencesRepository

/**
 * Use case for saving the last generated numbers ID to preferences.
 */
class SaveLastGeneratedNumbersIdUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(numbersId: String) {
        userPreferencesRepository.saveLastGeneratedNumbersId(numbersId)
    }
}
