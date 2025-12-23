package com.aaltix.lotto.core.domain.repository

import com.aaltix.lotto.core.domain.model.LotteryType

/**
 * Repository interface for lottery configuration operations.
 * Following Interface Segregation Principle - focused on configuration only.
 */
interface LotteryConfigRepository {
    /**
     * Get all available predefined lottery types.
     */
    suspend fun getLotteryTypes(): List<LotteryType>
}
