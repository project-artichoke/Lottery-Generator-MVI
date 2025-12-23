package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.repository.LotteryRepository
import kotlinx.coroutines.withContext

/**
 * Use case for getting a specific history entry by ID.
 */
class GetHistoryDetailUseCase(
    private val repository: LotteryRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(id: String): Result<GeneratedNumbers> {
        return withContext(dispatcherProvider.io) {
            try {
                val entry = repository.getHistoryById(id)
                if (entry != null) {
                    Result.Success(entry)
                } else {
                    Result.Error(NoSuchElementException("History entry not found"), "Entry not found")
                }
            } catch (e: Exception) {
                Result.Error(e, "Failed to get history entry")
            }
        }
    }
}
