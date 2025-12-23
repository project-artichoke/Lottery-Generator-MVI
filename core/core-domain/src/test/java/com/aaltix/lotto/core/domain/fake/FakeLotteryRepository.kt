package com.aaltix.lotto.core.domain.fake

import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.repository.LotteryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class FakeLotteryRepository : LotteryRepository {

    var shouldThrowOnGetTypes = false
    var shouldThrowOnGenerate = false
    private val historyEntries = MutableStateFlow<List<GeneratedNumbers>>(emptyList())
    var predefinedTypes: List<LotteryType> = TestLotteryTypes.ALL_TYPES

    override suspend fun getLotteryTypes(): List<LotteryType> {
        if (shouldThrowOnGetTypes) {
            throw RuntimeException("Config error")
        }
        return predefinedTypes
    }

    override suspend fun generateNumbers(lotteryType: LotteryType): GeneratedNumbers {
        if (shouldThrowOnGenerate) {
            throw RuntimeException("Generation error")
        }

        val mainNumbers = (1..lotteryType.mainNumberMax)
            .shuffled()
            .take(lotteryType.mainNumberCount)
            .sorted()

        val bonusNumbers = if (lotteryType.bonusNumberCount > 0) {
            (1..lotteryType.bonusNumberMax)
                .shuffled()
                .take(lotteryType.bonusNumberCount)
                .sorted()
        } else {
            emptyList()
        }

        val generated = GeneratedNumbers(
            id = UUID.randomUUID().toString(),
            lotteryType = lotteryType,
            mainNumbers = mainNumbers,
            bonusNumbers = bonusNumbers,
            timestamp = System.currentTimeMillis()
        )

        addTestEntry(generated)
        return generated
    }

    override fun getHistory(): Flow<List<GeneratedNumbers>> = historyEntries

    override fun getHistoryByType(lotteryTypeId: String): Flow<List<GeneratedNumbers>> {
        return historyEntries.map { list ->
            list.filter { it.lotteryType.id == lotteryTypeId }
        }
    }

    override suspend fun getHistoryById(id: String): GeneratedNumbers? {
        return historyEntries.value.find { it.id == id }
    }

    override suspend fun deleteHistory(id: String) {
        historyEntries.value = historyEntries.value.filterNot { it.id == id }
    }

    override suspend fun clearAllHistory() {
        historyEntries.value = emptyList()
    }

    override fun getHistoryPaged(limit: Int, offset: Int): Flow<List<GeneratedNumbers>> {
        return historyEntries.map { list ->
            list.drop(offset).take(limit)
        }
    }

    override suspend fun getHistoryCount(): Int = historyEntries.value.size

    fun addTestEntry(entry: GeneratedNumbers) {
        historyEntries.value = listOf(entry) + historyEntries.value
    }

    fun reset() {
        shouldThrowOnGetTypes = false
        shouldThrowOnGenerate = false
        historyEntries.value = emptyList()
    }
}

/**
 * Test lottery types for use in domain tests.
 */
object TestLotteryTypes {
    val POWERBALL = LotteryType(
        id = "powerball",
        name = "Powerball",
        displayName = "Powerball",
        mainNumberCount = 5,
        mainNumberMax = 69,
        bonusNumberCount = 1,
        bonusNumberMax = 26
    )

    val MEGA_MILLIONS = LotteryType(
        id = "mega_millions",
        name = "Mega Millions",
        displayName = "Mega Millions",
        mainNumberCount = 5,
        mainNumberMax = 70,
        bonusNumberCount = 1,
        bonusNumberMax = 25
    )

    val LOTTO_649 = LotteryType(
        id = "lotto_649",
        name = "Lotto 6/49",
        displayName = "Lotto 6/49",
        mainNumberCount = 6,
        mainNumberMax = 49,
        bonusNumberCount = 0,
        bonusNumberMax = 0
    )

    val ALL_TYPES = listOf(POWERBALL, MEGA_MILLIONS, LOTTO_649)
}
