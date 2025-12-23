package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.repository.CustomLotteryTypeRepository
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Use case for saving (creating or updating) a custom lottery type.
 * Includes validation logic for lottery type parameters.
 */
class SaveCustomLotteryTypeUseCase(
    private val repository: CustomLotteryTypeRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(lotteryType: LotteryType): Result<LotteryType> {
        return withContext(dispatcherProvider.io) {
            try {
                // Validation
                require(lotteryType.name.isNotBlank()) { "Name cannot be blank" }
                require(lotteryType.mainNumberCount in 1..10) { "Main number count must be between 1 and 10" }
                require(lotteryType.mainNumberMax in 1..99) { "Main number max must be between 1 and 99" }
                require(lotteryType.mainNumberCount <= lotteryType.mainNumberMax) {
                    "Main number count cannot exceed maximum value"
                }
                require(lotteryType.bonusNumberCount in 0..3) { "Bonus number count must be between 0 and 3" }

                if (lotteryType.bonusNumberCount > 0) {
                    require(lotteryType.bonusNumberMax in 1..99) {
                        "Bonus number max must be between 1 and 99"
                    }
                    require(lotteryType.bonusNumberCount <= lotteryType.bonusNumberMax) {
                        "Bonus number count cannot exceed maximum value"
                    }
                }

                // Generate ID if not provided (new type)
                val typeToSave = if (lotteryType.id.isBlank()) {
                    lotteryType.copy(
                        id = "custom_${UUID.randomUUID()}",
                        isCustom = true
                    )
                } else {
                    lotteryType.copy(isCustom = true)
                }

                repository.saveCustomLotteryType(typeToSave)
                Result.Success(typeToSave)
            } catch (e: IllegalArgumentException) {
                Result.Error(e, e.message ?: "Validation failed")
            } catch (e: Exception) {
                Result.Error(e, e.message ?: "Failed to save custom lottery type")
            }
        }
    }
}
