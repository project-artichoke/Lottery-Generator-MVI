package com.aaltix.lotto.core.domain.repository

import com.aaltix.lotto.core.domain.model.LotteryType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing custom lottery types.
 * Provides CRUD operations for user-created lottery configurations.
 */
interface CustomLotteryTypeRepository {
    /**
     * Get all custom lottery types as a Flow.
     */
    fun getCustomLotteryTypes(): Flow<List<LotteryType>>

    /**
     * Get the count of custom lottery types as a Flow.
     */
    fun getCustomLotteryTypesCount(): Flow<Int>

    /**
     * Get a custom lottery type by its ID.
     */
    suspend fun getCustomLotteryTypeById(id: String): LotteryType?

    /**
     * Save a custom lottery type (insert or update).
     */
    suspend fun saveCustomLotteryType(type: LotteryType)

    /**
     * Delete a custom lottery type by its ID.
     */
    suspend fun deleteCustomLotteryType(id: String)

    /**
     * Check if a custom lottery type with the given ID exists.
     */
    suspend fun customLotteryTypeExists(id: String): Boolean
}
