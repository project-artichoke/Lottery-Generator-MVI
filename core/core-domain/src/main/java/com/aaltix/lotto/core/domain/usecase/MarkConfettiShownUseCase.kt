package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.domain.repository.UserPreferencesRepository

/**
 * Use case for marking that confetti has been shown for a specific lottery type.
 */
class MarkConfettiShownUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(typeId: String) {
        userPreferencesRepository.markConfettiShownForType(typeId)
    }
}
