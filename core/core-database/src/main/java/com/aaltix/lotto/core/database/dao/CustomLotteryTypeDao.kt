package com.aaltix.lotto.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aaltix.lotto.core.database.entity.CustomLotteryTypeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for custom lottery types.
 * Provides CRUD operations for user-created lottery configurations.
 */
@Dao
interface CustomLotteryTypeDao {

    @Query("SELECT * FROM custom_lottery_types ORDER BY createdAt DESC")
    fun getAllCustomTypes(): Flow<List<CustomLotteryTypeEntity>>

    @Query("SELECT * FROM custom_lottery_types WHERE id = :id")
    suspend fun getById(id: String): CustomLotteryTypeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CustomLotteryTypeEntity)

    @Update
    suspend fun update(entity: CustomLotteryTypeEntity)

    @Query("DELETE FROM custom_lottery_types WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM custom_lottery_types WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Query("SELECT COUNT(*) FROM custom_lottery_types")
    fun getCount(): Flow<Int>
}
