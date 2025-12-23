package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.repository.LotteryRepository
import kotlinx.coroutines.withContext

/**
 * Use case for clearing all history entries.
 */
class ClearHistoryUseCase(
    private val repository: LotteryRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(): Result<Unit> {
        return withContext(dispatcherProvider.io) {
            try {
                repository.clearAllHistory()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e, "Failed to clear history")
            }
        }
    }
}
