package com.aaltix.lotto.core.domain.usecase

import app.cash.turbine.test
import com.aaltix.lotto.core.domain.fake.FakeLotteryRepository
import com.aaltix.lotto.core.domain.fake.TestLotteryTypes
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetHistoryUseCaseTest {

    private lateinit var useCase: GetHistoryUseCase
    private lateinit var fakeRepository: FakeLotteryRepository

    @Before
    fun setup() {
        fakeRepository = FakeLotteryRepository()
        useCase = GetHistoryUseCase(fakeRepository)
    }

    @Test
    fun `invoke returns empty list when no history`() = runTest {
        // Given - no entries added

        // When & Then
        useCase().test {
            val history = awaitItem()
            assertTrue(history.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke returns history entries`() = runTest {
        // Given
        fakeRepository.addTestEntry(createTestEntry("1", TestLotteryTypes.POWERBALL))
        fakeRepository.addTestEntry(createTestEntry("2", TestLotteryTypes.MEGA_MILLIONS))

        // When & Then
        useCase().test {
            val history = awaitItem()
            assertEquals(2, history.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `byType filters results by lotteryTypeId`() = runTest {
        // Given
        fakeRepository.addTestEntry(createTestEntry("1", TestLotteryTypes.POWERBALL))
        fakeRepository.addTestEntry(createTestEntry("2", TestLotteryTypes.POWERBALL))
        fakeRepository.addTestEntry(createTestEntry("3", TestLotteryTypes.MEGA_MILLIONS))

        // When & Then
        useCase.byType("powerball").test {
            val history = awaitItem()
            assertEquals(2, history.size)
            assertTrue(history.all { it.lotteryType.id == "powerball" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `history is ordered by timestamp descending`() = runTest {
        // Given
        val older = createTestEntry("1", TestLotteryTypes.POWERBALL, timestamp = 1000L)
        val newer = createTestEntry("2", TestLotteryTypes.POWERBALL, timestamp = 2000L)
        fakeRepository.addTestEntry(older)
        fakeRepository.addTestEntry(newer)

        // When & Then
        useCase().test {
            val history = awaitItem()
            // Newer entries should come first (added first in fake)
            assertEquals("2", history[0].id)
            assertEquals("1", history[1].id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestEntry(
        id: String,
        lotteryType: LotteryType,
        timestamp: Long = System.currentTimeMillis()
    ) = GeneratedNumbers(
        id = id,
        lotteryType = lotteryType,
        mainNumbers = listOf(1, 2, 3, 4, 5),
        bonusNumbers = if (lotteryType.bonusNumberCount > 0) listOf(10) else emptyList(),
        timestamp = timestamp
    )
}
