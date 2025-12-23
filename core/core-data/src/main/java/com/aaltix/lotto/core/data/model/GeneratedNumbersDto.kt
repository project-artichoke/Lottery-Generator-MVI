package com.aaltix.lotto.core.data.model

/**
 * Data Transfer Object for generated lottery numbers.
 */
data class GeneratedNumbersDto(
    val id: String,
    val lotteryType: LotteryTypeDto,
    val mainNumbers: List<Int>,
    val bonusNumbers: List<Int>,
    val timestamp: Long
)
