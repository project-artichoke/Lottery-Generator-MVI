package com.aaltix.lotto.core.data.fake

import com.aaltix.lotto.core.data.api.LotteryApi
import com.aaltix.lotto.core.data.model.GeneratedNumbersDto
import com.aaltix.lotto.core.data.model.LotteryTypeDto
import com.aaltix.lotto.core.data.model.LotteryTypesData

/**
 * Fake implementation of LotteryApi for testing.
 * Allows controlling behavior and verifying calls.
 */
class FakeLotteryApi : LotteryApi {

    var shouldThrowError = false
    var errorToThrow: Exception = RuntimeException("Test error")
    var generateNumbersCallCount = 0
    var lastGeneratedLotteryType: LotteryTypeDto? = null

    // Predefined numbers for predictable testing
    private val predefinedMainNumbers = listOf(1, 2, 3, 4, 5, 6)
    private val predefinedBonusNumbers = listOf(10)

    override suspend fun getLotteryTypes(): List<LotteryTypeDto> {
        if (shouldThrowError) throw errorToThrow
        return LotteryTypesData.ALL_TYPES
    }

    override suspend fun generateNumbers(lotteryType: LotteryTypeDto): GeneratedNumbersDto {
        generateNumbersCallCount++
        lastGeneratedLotteryType = lotteryType

        if (shouldThrowError) throw errorToThrow

        // Return predictable numbers for testing
        val mainNumbers = predefinedMainNumbers.take(lotteryType.mainNumberCount)
        val bonusNumbers = if (lotteryType.bonusNumberCount > 0) {
            predefinedBonusNumbers.take(lotteryType.bonusNumberCount)
        } else {
            emptyList()
        }

        return GeneratedNumbersDto(
            id = "test-id-${generateNumbersCallCount}",
            lotteryType = lotteryType,
            mainNumbers = mainNumbers,
            bonusNumbers = bonusNumbers,
            timestamp = System.currentTimeMillis()
        )
    }

    fun reset() {
        shouldThrowError = false
        errorToThrow = RuntimeException("Test error")
        generateNumbersCallCount = 0
        lastGeneratedLotteryType = null
    }
}
