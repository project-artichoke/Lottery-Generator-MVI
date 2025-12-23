package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.repository.LotteryRepository
import kotlinx.coroutines.withContext

/**
 * Use case for generating lottery numbers.
 */
class GenerateNumbersUseCase(
    private val repository: LotteryRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(lotteryType: LotteryType): Result<GeneratedNumbers> {
        return withContext(dispatcherProvider.io) {
            try {
                val result = repository.generateNumbers(lotteryType)
                Result.Success(result)
            } catch (e: Exception) {
                Result.Error(e, "Failed to generate numbers")
            }
        }
    }
}
