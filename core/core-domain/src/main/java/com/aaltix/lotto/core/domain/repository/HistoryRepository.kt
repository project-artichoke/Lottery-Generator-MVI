package com.aaltix.lotto.core.domain.repository

import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for history operations.
 * Following Interface Segregation Principle - focused on history management only.
 */
interface HistoryRepository {
    /**
     * Get all history entries as a Flow.
     */
    fun getHistory(): Flow<List<GeneratedNumbers>>

    /**
     * Get history entries filtered by lottery type.
     */
    fun getHistoryByType(lotteryTypeId: String): Flow<List<GeneratedNumbers>>

    /**
     * Get a specific history entry by ID.
     */
    suspend fun getHistoryById(id: String): GeneratedNumbers?

    /**
     * Delete a specific history entry.
     */
    suspend fun deleteHistory(id: String)

    /**
     * Clear all history entries.
     */
    suspend fun clearAllHistory()

    /**
     * Get paginated history entries.
     *
     * @param limit Maximum number of entries to return
     * @param offset Number of entries to skip
     */
    fun getHistoryPaged(limit: Int, offset: Int): Flow<List<GeneratedNumbers>>

    /**
     * Get total count of history entries.
     */
    suspend fun getHistoryCount(): Int
}
