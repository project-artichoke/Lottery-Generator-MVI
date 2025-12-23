package com.aaltix.lotto.core.data.mapper

import android.util.Log
import com.aaltix.lotto.core.data.model.GeneratedNumbersDto
import com.aaltix.lotto.core.data.model.LotteryTypeDto
import com.aaltix.lotto.core.data.model.LotteryTypesData
import com.aaltix.lotto.core.database.entity.HistoryEntryEntity
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType

/**
 * Mapper object for converting between data layer DTOs, database entities, and domain models.
 * Centralizes mapping logic and provides safe parsing with proper error handling.
 */
object DataMapper {

    private const val TAG = "DataMapper"
    private const val NUMBER_SEPARATOR = ","

    // ==================== Domain <-> DTO Mappings ====================

    /**
     * Converts a LotteryType domain model to a LotteryTypeDto.
     */
    fun LotteryType.toDto(): LotteryTypeDto = LotteryTypeDto(
        id = id,
        name = name,
        displayName = displayName,
        mainNumberCount = mainNumberCount,
        mainNumberMax = mainNumberMax,
        bonusNumberCount = bonusNumberCount,
        bonusNumberMax = bonusNumberMax,
        isCustom = isCustom
    )

    /**
     * Converts a LotteryTypeDto to a LotteryType domain model.
     */
    fun LotteryTypeDto.toDomain(): LotteryType = LotteryType(
        id = id,
        name = name,
        displayName = displayName,
        mainNumberCount = mainNumberCount,
        mainNumberMax = mainNumberMax,
        bonusNumberCount = bonusNumberCount,
        bonusNumberMax = bonusNumberMax,
        isCustom = isCustom
    )

    /**
     * Converts a GeneratedNumbersDto to a GeneratedNumbers domain model.
     */
    fun GeneratedNumbersDto.toDomain(): GeneratedNumbers = GeneratedNumbers(
        id = id,
        lotteryType = lotteryType.toDomain(),
        mainNumbers = mainNumbers,
        bonusNumbers = bonusNumbers,
        timestamp = timestamp
    )

    /**
     * Converts a list of GeneratedNumbersDto to domain models.
     */
    fun List<GeneratedNumbersDto>.toDomainList(): List<GeneratedNumbers> = map { it.toDomain() }

    /**
     * Converts a list of LotteryTypeDto to domain models.
     */
    fun List<LotteryTypeDto>.toDomainTypeList(): List<LotteryType> = map { it.toDomain() }

    // ==================== DTO <-> Entity Mappings ====================

    /**
     * Converts a GeneratedNumbersDto to a HistoryEntryEntity for database storage.
     */
    fun GeneratedNumbersDto.toEntity(): HistoryEntryEntity {
        return HistoryEntryEntity(
            id = id,
            lotteryTypeId = lotteryType.id,
            lotteryTypeName = lotteryType.displayName,
            lotteryTypeMainNumberCount = lotteryType.mainNumberCount,
            lotteryTypeMainNumberMax = lotteryType.mainNumberMax,
            lotteryTypeBonusNumberCount = lotteryType.bonusNumberCount,
            lotteryTypeBonusNumberMax = lotteryType.bonusNumberMax,
            isCustomLotteryType = lotteryType.isCustom,
            mainNumbers = mainNumbers.joinToString(NUMBER_SEPARATOR),
            bonusNumbers = bonusNumbers.joinToString(NUMBER_SEPARATOR),
            timestamp = timestamp
        )
    }

    /**
     * Converts a HistoryEntryEntity to a GeneratedNumbersDto.
     * Uses safe parsing for numbers and logs warnings for unknown lottery types.
     */
    fun HistoryEntryEntity.toDto(): GeneratedNumbersDto {
        val lotteryType = LotteryTypesData.ALL_TYPES.find { it.id == lotteryTypeId }
            ?: run {
                if (!isCustomLotteryType) {
                    Log.w(TAG, "Unknown lottery type ID: $lotteryTypeId, using stored snapshot")
                }
                LotteryTypeDto(
                    id = lotteryTypeId,
                    name = lotteryTypeName,
                    displayName = lotteryTypeName,
                    mainNumberCount = lotteryTypeMainNumberCount,
                    mainNumberMax = lotteryTypeMainNumberMax,
                    bonusNumberCount = lotteryTypeBonusNumberCount,
                    bonusNumberMax = lotteryTypeBonusNumberMax,
                    isCustom = isCustomLotteryType
                )
            }

        return GeneratedNumbersDto(
            id = id,
            lotteryType = lotteryType,
            mainNumbers = parseNumbers(mainNumbers, "mainNumbers", id),
            bonusNumbers = parseNumbers(bonusNumbers, "bonusNumbers", id),
            timestamp = timestamp
        )
    }

    /**
     * Safely parses a comma-separated string of numbers.
     * Logs warnings for any values that cannot be parsed.
     *
     * @param numbersString The comma-separated string of numbers
     * @param fieldName The field name for logging purposes
     * @param entryId The entry ID for logging purposes
     * @return List of successfully parsed integers
     */
    private fun parseNumbers(numbersString: String, fieldName: String, entryId: String): List<Int> {
        if (numbersString.isBlank()) return emptyList()

        return numbersString
            .split(NUMBER_SEPARATOR)
            .filter { it.isNotBlank() }
            .mapNotNull { value ->
                value.trim().toIntOrNull().also { parsed ->
                    if (parsed == null) {
                        Log.w(TAG, "Failed to parse $fieldName value '$value' for entry $entryId")
                    }
                }
            }
    }
}
