package com.aaltix.lotto.core.domain.repository

import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType

/**
 * Repository interface for number generation operations.
 * Following Interface Segregation Principle - focused on generation only.
 */
interface NumberGeneratorRepository {
    /**
     * Generate random numbers for the given lottery type and save to history.
     */
    suspend fun generateNumbers(lotteryType: LotteryType): GeneratedNumbers
}
