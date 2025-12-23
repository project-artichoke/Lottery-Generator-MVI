package com.aaltix.lotto.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aaltix.lotto.core.database.dao.CustomLotteryTypeDao
import com.aaltix.lotto.core.database.dao.HistoryDao
import com.aaltix.lotto.core.database.entity.CustomLotteryTypeEntity
import com.aaltix.lotto.core.database.entity.HistoryEntryEntity

/**
 * Room database for the Lottery Generator app.
 */
@Database(
    entities = [HistoryEntryEntity::class, CustomLotteryTypeEntity::class],
    version = 3,
    exportSchema = true
)
abstract class LottoDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun customLotteryTypeDao(): CustomLotteryTypeDao

    companion object {
        const val DATABASE_NAME = "lotto_database"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS custom_lottery_types (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        displayName TEXT NOT NULL,
                        mainNumberCount INTEGER NOT NULL,
                        mainNumberMax INTEGER NOT NULL,
                        bonusNumberCount INTEGER NOT NULL,
                        bonusNumberMax INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    ALTER TABLE history_entries ADD COLUMN lotteryTypeMainNumberCount INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    ALTER TABLE history_entries ADD COLUMN lotteryTypeMainNumberMax INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    ALTER TABLE history_entries ADD COLUMN lotteryTypeBonusNumberCount INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    ALTER TABLE history_entries ADD COLUMN lotteryTypeBonusNumberMax INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    ALTER TABLE history_entries ADD COLUMN isCustomLotteryType INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    UPDATE history_entries SET
                        lotteryTypeMainNumberCount = CASE lotteryTypeId
                            WHEN 'powerball' THEN 5
                            WHEN 'mega_millions' THEN 5
                            WHEN 'lotto_6_49' THEN 6
                            ELSE lotteryTypeMainNumberCount
                        END,
                        lotteryTypeMainNumberMax = CASE lotteryTypeId
                            WHEN 'powerball' THEN 69
                            WHEN 'mega_millions' THEN 70
                            WHEN 'lotto_6_49' THEN 49
                            ELSE lotteryTypeMainNumberMax
                        END,
                        lotteryTypeBonusNumberCount = CASE lotteryTypeId
                            WHEN 'powerball' THEN 1
                            WHEN 'mega_millions' THEN 1
                            WHEN 'lotto_6_49' THEN 0
                            ELSE lotteryTypeBonusNumberCount
                        END,
                        lotteryTypeBonusNumberMax = CASE lotteryTypeId
                            WHEN 'powerball' THEN 26
                            WHEN 'mega_millions' THEN 25
                            WHEN 'lotto_6_49' THEN 0
                            ELSE lotteryTypeBonusNumberMax
                        END,
                        isCustomLotteryType = CASE
                            WHEN lotteryTypeId IN ('powerball','mega_millions','lotto_6_49') THEN 0
                            ELSE isCustomLotteryType
                        END
                    """.trimIndent()
                )
            }
        }
    }
}
