package com.aaltix.lotto.core.data.model

/**
 * Data Transfer Object for lottery type configuration.
 */
data class LotteryTypeDto(
    val id: String,
    val name: String,
    val displayName: String,
    val mainNumberCount: Int,
    val mainNumberMax: Int,
    val bonusNumberCount: Int,
    val bonusNumberMax: Int,
    val isCustom: Boolean = false
)

/**
 * Predefined lottery types available in the app.
 */
object LotteryTypesData {
    val POWERBALL = LotteryTypeDto(
        id = "powerball",
        name = "Powerball",
        displayName = "Powerball",
        mainNumberCount = 5,
        mainNumberMax = 69,
        bonusNumberCount = 1,
        bonusNumberMax = 26,
        isCustom = false
    )

    val MEGA_MILLIONS = LotteryTypeDto(
        id = "mega_millions",
        name = "Mega Millions",
        displayName = "Mega Millions",
        mainNumberCount = 5,
        mainNumberMax = 70,
        bonusNumberCount = 1,
        bonusNumberMax = 25,
        isCustom = false
    )

    val LOTTO_6_49 = LotteryTypeDto(
        id = "lotto_6_49",
        name = "Lotto 6/49",
        displayName = "Classic 6/49",
        mainNumberCount = 6,
        mainNumberMax = 49,
        bonusNumberCount = 0,
        bonusNumberMax = 0,
        isCustom = false
    )

    val CASH4LIFE = LotteryTypeDto(
        id = "cash4life",
        name = "Cash4Life",
        displayName = "Cash4Life",
        mainNumberCount = 5,
        mainNumberMax = 60,
        bonusNumberCount = 1,
        bonusNumberMax = 4,
        isCustom = false
    )

    val LUCKY_FOR_LIFE = LotteryTypeDto(
        id = "lucky_for_life",
        name = "Lucky for Life",
        displayName = "Lucky for Life",
        mainNumberCount = 5,
        mainNumberMax = 48,
        bonusNumberCount = 1,
        bonusNumberMax = 18,
        isCustom = false
    )

    val LOTTO_AMERICA = LotteryTypeDto(
        id = "lotto_america",
        name = "Lotto America",
        displayName = "Lotto America",
        mainNumberCount = 5,
        mainNumberMax = 52,
        bonusNumberCount = 1,
        bonusNumberMax = 10,
        isCustom = false
    )

    val PICK_3 = LotteryTypeDto(
        id = "pick_3",
        name = "Pick 3",
        displayName = "Pick 3",
        mainNumberCount = 3,
        mainNumberMax = 9,
        bonusNumberCount = 0,
        bonusNumberMax = 0,
        isCustom = false
    )

    val PICK_4 = LotteryTypeDto(
        id = "pick_4",
        name = "Pick 4",
        displayName = "Pick 4",
        mainNumberCount = 4,
        mainNumberMax = 9,
        bonusNumberCount = 0,
        bonusNumberMax = 0,
        isCustom = false
    )

    val ALL_TYPES = listOf(
        POWERBALL,
        MEGA_MILLIONS,
        CASH4LIFE,
        LUCKY_FOR_LIFE,
        LOTTO_AMERICA,
        LOTTO_6_49,
        PICK_3,
        PICK_4
    )
}
