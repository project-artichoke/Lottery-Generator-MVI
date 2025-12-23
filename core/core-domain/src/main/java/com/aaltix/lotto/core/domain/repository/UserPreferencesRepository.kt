package com.aaltix.lotto.core.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for storing user preferences.
 * Abstracts the underlying storage mechanism (DataStore, SharedPreferences, etc.)
 */
interface UserPreferencesRepository {
    /**
     * Check if the user has accepted the disclaimer.
     */
    fun hasAcceptedDisclaimer(): Flow<Boolean>

    /**
     * Mark the disclaimer as accepted.
     */
    suspend fun acceptDisclaimer()

    /**
     * Get the last selected lottery type ID.
     */
    fun getSelectedLotteryTypeId(): Flow<String?>

    /**
     * Save the selected lottery type ID.
     */
    suspend fun saveSelectedLotteryTypeId(typeId: String)

    /**
     * Get the last generated numbers ID.
     */
    fun getLastGeneratedNumbersId(): Flow<String?>

    /**
     * Save the last generated numbers ID.
     */
    suspend fun saveLastGeneratedNumbersId(numbersId: String)

    /**
     * Clear the last generated numbers (e.g., when switching lottery types).
     */
    suspend fun clearLastGeneratedNumbers()

    /**
     * Check if confetti has been shown for a specific lottery type.
     * Used to show confetti only on first generation for each type.
     */
    suspend fun hasShownConfettiForType(typeId: String): Boolean

    /**
     * Mark that confetti has been shown for a specific lottery type.
     */
    suspend fun markConfettiShownForType(typeId: String)
}
