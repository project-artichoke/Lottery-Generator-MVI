package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting the last generated numbers ID from preferences.
 */
class GetLastGeneratedNumbersIdUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<String?> {
        return userPreferencesRepository.getLastGeneratedNumbersId()
    }
}
