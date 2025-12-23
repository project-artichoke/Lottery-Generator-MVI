package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.repository.CustomLotteryTypeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting all custom lottery types as a Flow.
 */
class GetCustomLotteryTypesUseCase(
    private val repository: CustomLotteryTypeRepository
) {
    operator fun invoke(): Flow<List<LotteryType>> {
        return repository.getCustomLotteryTypes()
    }
}
