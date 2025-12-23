package com.aaltix.lotto.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for user-created custom lottery types.
 * Stores configuration for custom lottery games that users can create.
 */
@Entity(tableName = "custom_lottery_types")
data class CustomLotteryTypeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val displayName: String,
    val mainNumberCount: Int,
    val mainNumberMax: Int,
    val bonusNumberCount: Int,
    val bonusNumberMax: Int,
    val createdAt: Long,
    val updatedAt: Long
)
