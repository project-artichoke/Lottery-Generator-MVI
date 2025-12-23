package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.repository.CustomLotteryTypeRepository
import kotlinx.coroutines.withContext

/**
 * Use case for deleting a custom lottery type.
 */
class DeleteCustomLotteryTypeUseCase(
    private val repository: CustomLotteryTypeRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return withContext(dispatcherProvider.io) {
            try {
                repository.deleteCustomLotteryType(id)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e, e.message ?: "Failed to delete custom lottery type")
            }
        }
    }
}
