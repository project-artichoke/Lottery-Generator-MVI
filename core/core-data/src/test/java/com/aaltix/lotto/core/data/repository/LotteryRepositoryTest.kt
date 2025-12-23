package com.aaltix.lotto.core.data.repository

import app.cash.turbine.test
import com.aaltix.lotto.core.data.fake.FakeHistoryDao
import com.aaltix.lotto.core.data.fake.FakeLotteryApi
import com.aaltix.lotto.core.data.mapper.DataMapper.toDomain
import com.aaltix.lotto.core.data.model.LotteryTypesData
import com.aaltix.lotto.core.domain.model.LotteryType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for LotteryRepositoryImpl.
 * Tests cover:
 * - Number generation and history saving
 * - Lottery types retrieval
 * - History operations (get, filter, delete, clear)
 * - Error handling
 * - Pagination
 */
class LotteryRepositoryTest {

    private lateinit var repository: LotteryRepositoryImpl
    private lateinit var fakeApi: FakeLotteryApi
    private lateinit var fakeDao: FakeHistoryDao

    // Domain model versions of lottery types for test use
    private val powerball: LotteryType = LotteryTypesData.POWERBALL.toDomain()
    private val megaMillions: LotteryType = LotteryTypesData.MEGA_MILLIONS.toDomain()
    private val lotto649: LotteryType = LotteryTypesData.LOTTO_6_49.toDomain()

    @Before
    fun setup() {
        fakeApi = FakeLotteryApi()
        fakeDao = FakeHistoryDao()
        repository = LotteryRepositoryImpl(fakeApi, fakeDao)
    }

    // ==================== LOTTERY TYPES TESTS ====================

    @Test
    fun `getLotteryTypes returns lottery types`() = runTest {
        val types = repository.getLotteryTypes()

        assertEquals(8, types.size)
        assertEquals("powerball", types[0].id)
    }

    @Test(expected = RuntimeException::class)
    fun `getLotteryTypes throws when api fails`() = runTest {
        fakeApi.shouldThrowError = true

        repository.getLotteryTypes()
    }

    // ==================== GENERATE NUMBERS TESTS ====================

    @Test
    fun `generateNumbers returns numbers and saves to history`() = runTest {
        val result = repository.generateNumbers(powerball)

        assertNotNull(result.id)
        assertEquals(powerball, result.lotteryType)
        assertEquals(5, result.mainNumbers.size)
        assertEquals(1, result.bonusNumbers.size)

        // Verify saved to history
        assertEquals(1, fakeDao.getEntriesCount())
    }

    @Test
    fun `generateNumbers tracks call count`() = runTest {
        repository.generateNumbers(powerball)
        repository.generateNumbers(megaMillions)
        repository.generateNumbers(lotto649)

        assertEquals(3, fakeApi.generateNumbersCallCount)
    }

    @Test
    fun `generateNumbers tracks last lottery type`() = runTest {
        repository.generateNumbers(megaMillions)

        assertEquals(LotteryTypesData.MEGA_MILLIONS, fakeApi.lastGeneratedLotteryType)
    }

    @Test(expected = RuntimeException::class)
    fun `generateNumbers throws when api fails`() = runTest {
        fakeApi.shouldThrowError = true

        repository.generateNumbers(powerball)
    }

    @Test
    fun `generateNumbers does not save to history on api failure`() = runTest {
        fakeApi.shouldThrowError = true

        try {
            repository.generateNumbers(powerball)
        } catch (_: Exception) {
            // Expected
        }

        assertEquals(0, fakeDao.getEntriesCount())
    }

    // ==================== GET HISTORY TESTS ====================

    @Test
    fun `getHistory returns flow of history entries`() = runTest {
        // Generate some numbers first
        repository.generateNumbers(powerball)
        repository.generateNumbers(megaMillions)

        repository.getHistory().test {
            val history = awaitItem()
            assertEquals(2, history.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getHistory returns empty flow when no history`() = runTest {
        repository.getHistory().test {
            val history = awaitItem()
            assertTrue(history.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== GET HISTORY BY TYPE TESTS ====================

    @Test
    fun `getHistoryByType filters by lottery type`() = runTest {
        repository.generateNumbers(powerball)
        repository.generateNumbers(powerball)
        repository.generateNumbers(megaMillions)

        repository.getHistoryByType("powerball").test {
            val history = awaitItem()
            assertEquals(2, history.size)
            assertTrue(history.all { it.lotteryType.id == "powerball" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getHistoryByType returns empty for non-matching type`() = runTest {
        repository.generateNumbers(powerball)

        repository.getHistoryByType("mega_millions").test {
            val history = awaitItem()
            assertTrue(history.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== GET HISTORY BY ID TESTS ====================

    @Test
    fun `getHistoryById returns correct entry`() = runTest {
        val generated = repository.generateNumbers(powerball)

        val result = repository.getHistoryById(generated.id)

        assertNotNull(result)
        assertEquals(generated.id, result?.id)
    }

    @Test
    fun `getHistoryById returns null for non-existent id`() = runTest {
        val result = repository.getHistoryById("non-existent-id")

        assertNull(result)
    }

    // ==================== DELETE HISTORY TESTS ====================

    @Test
    fun `deleteHistory removes entry`() = runTest {
        val generated = repository.generateNumbers(powerball)
        assertEquals(1, fakeDao.getEntriesCount())

        repository.deleteHistory(generated.id)

        assertEquals(0, fakeDao.getEntriesCount())
    }

    @Test
    fun `deleteHistory does nothing for non-existent id`() = runTest {
        repository.generateNumbers(powerball)
        assertEquals(1, fakeDao.getEntriesCount())

        repository.deleteHistory("non-existent-id")

        assertEquals(1, fakeDao.getEntriesCount())
    }

    // ==================== CLEAR ALL HISTORY TESTS ====================

    @Test
    fun `clearAllHistory removes all entries`() = runTest {
        repository.generateNumbers(powerball)
        repository.generateNumbers(megaMillions)
        assertEquals(2, fakeDao.getEntriesCount())

        repository.clearAllHistory()

        assertEquals(0, fakeDao.getEntriesCount())
    }

    @Test
    fun `clearAllHistory works on empty history`() = runTest {
        assertEquals(0, fakeDao.getEntriesCount())

        repository.clearAllHistory()

        assertEquals(0, fakeDao.getEntriesCount())
    }

    // ==================== PAGINATION TESTS ====================

    @Test
    fun `getHistoryPaged returns limited entries`() = runTest {
        repeat(5) {
            repository.generateNumbers(powerball)
        }

        repository.getHistoryPaged(limit = 3, offset = 0).test {
            val history = awaitItem()
            assertEquals(3, history.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getHistoryPaged respects offset`() = runTest {
        repeat(5) {
            repository.generateNumbers(powerball)
        }

        repository.getHistoryPaged(limit = 2, offset = 2).test {
            val history = awaitItem()
            assertEquals(2, history.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getHistoryPaged returns empty when offset exceeds entries`() = runTest {
        repeat(3) {
            repository.generateNumbers(powerball)
        }

        repository.getHistoryPaged(limit = 10, offset = 10).test {
            val history = awaitItem()
            assertTrue(history.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getHistoryCount returns correct count`() = runTest {
        repository.generateNumbers(powerball)
        repository.generateNumbers(megaMillions)
        repository.generateNumbers(lotto649)

        val count = repository.getHistoryCount()

        assertEquals(3, count)
    }

    @Test
    fun `getHistoryCount returns zero for empty history`() = runTest {
        val count = repository.getHistoryCount()

        assertEquals(0, count)
    }

    // ==================== FAKE API RESET TEST ====================

    @Test
    fun `fake api reset clears state`() = runTest {
        fakeApi.shouldThrowError = true
        fakeApi.generateNumbersCallCount = 5

        fakeApi.reset()

        assertEquals(false, fakeApi.shouldThrowError)
        assertEquals(0, fakeApi.generateNumbersCallCount)
        assertNull(fakeApi.lastGeneratedLotteryType)
    }
}
