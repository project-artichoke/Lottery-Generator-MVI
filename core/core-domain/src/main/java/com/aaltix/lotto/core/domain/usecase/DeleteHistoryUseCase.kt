package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.repository.LotteryRepository
import kotlinx.coroutines.withContext

/**
 * Use case for deleting a specific history entry.
 */
class DeleteHistoryUseCase(
    private val repository: LotteryRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return withContext(dispatcherProvider.io) {
            try {
                repository.deleteHistory(id)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e, "Failed to delete history entry")
            }
        }
    }
}
