package com.aaltix.lotto.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a history entry of generated lottery numbers.
 */
@Entity(tableName = "history_entries")
data class HistoryEntryEntity(
    @PrimaryKey
    val id: String,
    val lotteryTypeId: String,
    val lotteryTypeName: String,
    val lotteryTypeMainNumberCount: Int,
    val lotteryTypeMainNumberMax: Int,
    val lotteryTypeBonusNumberCount: Int,
    val lotteryTypeBonusNumberMax: Int,
    val isCustomLotteryType: Boolean,
    val mainNumbers: String, // Stored as comma-separated values
    val bonusNumbers: String, // Stored as comma-separated values (empty if none)
    val timestamp: Long
)
