package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.repository.CustomLotteryTypeRepository
import kotlinx.coroutines.withContext

/**
 * Use case for getting a custom lottery type by its ID.
 */
class GetCustomLotteryTypeByIdUseCase(
    private val repository: CustomLotteryTypeRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(id: String): Result<LotteryType?> {
        return withContext(dispatcherProvider.io) {
            try {
                val type = repository.getCustomLotteryTypeById(id)
                Result.Success(type)
            } catch (e: Exception) {
                Result.Error(e, e.message ?: "Failed to get custom lottery type")
            }
        }
    }
}
