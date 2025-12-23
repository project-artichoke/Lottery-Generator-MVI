package com.aaltix.lotto.core.data.mapper

import com.aaltix.lotto.core.data.model.LotteryTypeDto
import com.aaltix.lotto.core.database.entity.CustomLotteryTypeEntity
import com.aaltix.lotto.core.domain.model.LotteryType

/**
 * Mapper functions for converting between CustomLotteryTypeEntity, LotteryTypeDto, and domain models.
 */
object CustomLotteryTypeMapper {

    /**
     * Convert a CustomLotteryTypeEntity to LotteryTypeDto.
     */
    fun CustomLotteryTypeEntity.toDto(): LotteryTypeDto = LotteryTypeDto(
        id = id,
        name = name,
        displayName = displayName,
        mainNumberCount = mainNumberCount,
        mainNumberMax = mainNumberMax,
        bonusNumberCount = bonusNumberCount,
        bonusNumberMax = bonusNumberMax,
        isCustom = true
    )

    /**
     * Convert a CustomLotteryTypeEntity to a LotteryType domain model.
     */
    fun CustomLotteryTypeEntity.toDomain(): LotteryType = LotteryType(
        id = id,
        name = name,
        displayName = displayName,
        mainNumberCount = mainNumberCount,
        mainNumberMax = mainNumberMax,
        bonusNumberCount = bonusNumberCount,
        bonusNumberMax = bonusNumberMax,
        isCustom = true
    )

    /**
     * Convert a LotteryTypeDto to CustomLotteryTypeEntity.
     * Note: createdAt and updatedAt must be provided separately.
     */
    fun LotteryTypeDto.toEntity(
        createdAt: Long,
        updatedAt: Long
    ): CustomLotteryTypeEntity = CustomLotteryTypeEntity(
        id = id,
        name = name,
        displayName = displayName,
        mainNumberCount = mainNumberCount,
        mainNumberMax = mainNumberMax,
        bonusNumberCount = bonusNumberCount,
        bonusNumberMax = bonusNumberMax,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    /**
     * Convert a LotteryType domain model to CustomLotteryTypeEntity.
     * Note: createdAt and updatedAt must be provided separately.
     */
    fun LotteryType.toEntity(
        createdAt: Long,
        updatedAt: Long
    ): CustomLotteryTypeEntity = CustomLotteryTypeEntity(
        id = id,
        name = name,
        displayName = displayName,
        mainNumberCount = mainNumberCount,
        mainNumberMax = mainNumberMax,
        bonusNumberCount = bonusNumberCount,
        bonusNumberMax = bonusNumberMax,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
