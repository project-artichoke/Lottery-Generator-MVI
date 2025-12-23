package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.repository.CustomLotteryTypeRepository
import com.aaltix.lotto.core.domain.repository.LotteryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Use case for getting all available lottery types (predefined + custom).
 */
class GetLotteryTypesUseCase(
    private val repository: LotteryRepository,
    private val customRepository: CustomLotteryTypeRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(): Result<List<LotteryType>> {
        return withContext(dispatcherProvider.io) {
            try {
                // Get predefined types
                val predefinedTypes = repository.getLotteryTypes()

                // Get custom types
                val customTypes = customRepository.getCustomLotteryTypes().first()

                // Merge: predefined first, then custom
                Result.Success(predefinedTypes + customTypes)
            } catch (e: Exception) {
                Result.Error(e, "Failed to get lottery types")
            }
        }
    }
}
