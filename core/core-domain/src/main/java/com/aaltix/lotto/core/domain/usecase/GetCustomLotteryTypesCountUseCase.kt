package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.domain.repository.CustomLotteryTypeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting the count of custom lottery types as a Flow.
 */
class GetCustomLotteryTypesCountUseCase(
    private val repository: CustomLotteryTypeRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getCustomLotteryTypesCount()
    }
}
