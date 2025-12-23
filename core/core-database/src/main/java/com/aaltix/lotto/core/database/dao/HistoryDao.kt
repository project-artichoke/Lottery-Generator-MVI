package com.aaltix.lotto.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aaltix.lotto.core.database.entity.HistoryEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for history entries.
 */
@Dao
interface HistoryDao {

    @Query("SELECT * FROM history_entries ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntryEntity>>

    @Query("SELECT * FROM history_entries ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    fun getHistoryPaged(limit: Int, offset: Int): Flow<List<HistoryEntryEntity>>

    @Query("SELECT * FROM history_entries WHERE lotteryTypeId = :lotteryTypeId ORDER BY timestamp DESC")
    fun getHistoryByType(lotteryTypeId: String): Flow<List<HistoryEntryEntity>>

    @Query("SELECT * FROM history_entries WHERE id = :id")
    suspend fun getHistoryById(id: String): HistoryEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entry: HistoryEntryEntity)

    @Delete
    suspend fun deleteHistory(entry: HistoryEntryEntity)

    @Query("DELETE FROM history_entries WHERE id = :id")
    suspend fun deleteHistoryById(id: String)

    @Query("DELETE FROM history_entries")
    suspend fun clearAllHistory()

    @Query("SELECT COUNT(*) FROM history_entries")
    suspend fun getHistoryCount(): Int
}
