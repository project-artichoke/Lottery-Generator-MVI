package com.aaltix.lotto.core.data.api

import com.aaltix.lotto.core.data.model.GeneratedNumbersDto
import com.aaltix.lotto.core.data.model.LotteryTypeDto
import com.aaltix.lotto.core.data.model.LotteryTypesData
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Stubbed implementation of LotteryApi that simulates network delay
 * and generates random lottery numbers.
 */
class StubbedLotteryApi : LotteryApi {

    companion object {
        private const val SIMULATED_DELAY_MS = 800L
    }

    override suspend fun generateNumbers(lotteryType: LotteryTypeDto): GeneratedNumbersDto {
        // Simulate network delay
        delay(SIMULATED_DELAY_MS)

        // Pick games allow duplicate digits and start from 0
        val isPickGame = lotteryType.id.startsWith("pick_")

        val mainNumbers = if (isPickGame) {
            generatePickNumbers(
                count = lotteryType.mainNumberCount,
                max = lotteryType.mainNumberMax
            )
        } else {
            generateUniqueRandomNumbers(
                count = lotteryType.mainNumberCount,
                max = lotteryType.mainNumberMax
            )
        }

        val bonusNumbers = if (lotteryType.bonusNumberCount > 0) {
            generateUniqueRandomNumbers(
                count = lotteryType.bonusNumberCount,
                max = lotteryType.bonusNumberMax
            )
        } else {
            emptyList()
        }

        return GeneratedNumbersDto(
            id = UUID.randomUUID().toString(),
            lotteryType = lotteryType,
            mainNumbers = if (isPickGame) mainNumbers else mainNumbers.sorted(),
            bonusNumbers = bonusNumbers.sorted(),
            timestamp = System.currentTimeMillis()
        )
    }

    override suspend fun getLotteryTypes(): List<LotteryTypeDto> {
        // Simulate network delay
        delay(SIMULATED_DELAY_MS / 2)
        return LotteryTypesData.ALL_TYPES
    }

    /**
     * Generate a list of unique random numbers from 1 to max.
     */
    private fun generateUniqueRandomNumbers(count: Int, max: Int): List<Int> {
        require(count <= max) { "Cannot generate $count unique numbers from 1 to $max" }
        return (1..max).shuffled().take(count)
    }

    /**
     * Generate random digits for Pick games (0 to max, duplicates allowed).
     * Numbers are not sorted to maintain the order of selection.
     */
    private fun generatePickNumbers(count: Int, max: Int): List<Int> {
        return List(count) { (0..max).random() }
    }
}
