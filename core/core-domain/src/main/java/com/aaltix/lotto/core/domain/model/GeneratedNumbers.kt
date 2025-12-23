package com.aaltix.lotto.core.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Domain model representing generated lottery numbers.
 */
data class GeneratedNumbers(
    val id: String,
    val lotteryType: LotteryType,
    val mainNumbers: List<Int>,
    val bonusNumbers: List<Int>,
    val timestamp: Long
) {
    val formattedDate: String
        get() {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }

    val mainNumbersFormatted: String
        get() = mainNumbers.joinToString(" - ")

    val bonusNumbersFormatted: String
        get() = if (bonusNumbers.isNotEmpty()) {
            bonusNumbers.joinToString(" - ")
        } else {
            ""
        }

    val hasBonusNumbers: Boolean
        get() = bonusNumbers.isNotEmpty()
}
