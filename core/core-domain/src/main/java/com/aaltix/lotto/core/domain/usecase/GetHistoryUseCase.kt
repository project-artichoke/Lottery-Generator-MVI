package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.repository.LotteryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting lottery history.
 */
class GetHistoryUseCase(
    private val repository: LotteryRepository
) {
    operator fun invoke(): Flow<List<GeneratedNumbers>> {
        return repository.getHistory()
    }

    fun byType(lotteryTypeId: String): Flow<List<GeneratedNumbers>> {
        return repository.getHistoryByType(lotteryTypeId)
    }
}
