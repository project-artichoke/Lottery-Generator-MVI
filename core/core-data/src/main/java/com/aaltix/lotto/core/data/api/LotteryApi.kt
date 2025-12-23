package com.aaltix.lotto.core.data.api

import com.aaltix.lotto.core.data.model.GeneratedNumbersDto
import com.aaltix.lotto.core.data.model.LotteryTypeDto

/**
 * API interface for lottery operations.
 * Can be implemented with a real network client or stubbed for testing.
 */
interface LotteryApi {
    /**
     * Generate random lottery numbers for the given lottery type.
     */
    suspend fun generateNumbers(lotteryType: LotteryTypeDto): GeneratedNumbersDto

    /**
     * Get all available lottery types.
     */
    suspend fun getLotteryTypes(): List<LotteryTypeDto>
}
