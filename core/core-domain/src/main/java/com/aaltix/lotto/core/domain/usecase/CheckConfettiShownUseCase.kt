package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.domain.repository.UserPreferencesRepository

/**
 * Use case for checking if confetti has been shown for a specific lottery type.
 * Used to show confetti only on first generation for each type.
 */
class CheckConfettiShownUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(typeId: String): Boolean {
        return userPreferencesRepository.hasShownConfettiForType(typeId)
    }
}
