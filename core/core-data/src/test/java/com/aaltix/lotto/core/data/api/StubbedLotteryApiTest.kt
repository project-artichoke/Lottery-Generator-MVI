package com.aaltix.lotto.core.data.api

import com.aaltix.lotto.core.data.model.LotteryTypeDto
import com.aaltix.lotto.core.data.model.LotteryTypesData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive unit tests for the number generation logic in StubbedLotteryApi.
 * Tests cover:
 * - Correct number counts for each lottery type
 * - Numbers within valid ranges
 * - Uniqueness of generated numbers
 * - Sorting of generated numbers
 * - Edge cases and boundary conditions
 */
class StubbedLotteryApiTest {

    private lateinit var api: StubbedLotteryApi

    @Before
    fun setup() {
        api = StubbedLotteryApi()
    }

    // ==================== POWERBALL TESTS ====================

    @Test
    fun `generateNumbers for Powerball returns correct main number count`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.POWERBALL)

        assertEquals(5, result.mainNumbers.size)
    }

    @Test
    fun `generateNumbers for Powerball returns correct bonus number count`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.POWERBALL)

        assertEquals(1, result.bonusNumbers.size)
    }

    @Test
    fun `generateNumbers for Powerball main numbers are within valid range 1-69`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.POWERBALL)

        assertTrue(
            "All main numbers should be between 1 and 69",
            result.mainNumbers.all { it in 1..69 }
        )
    }

    @Test
    fun `generateNumbers for Powerball bonus number is within valid range 1-26`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.POWERBALL)

        assertTrue(
            "Bonus number should be between 1 and 26",
            result.bonusNumbers.all { it in 1..26 }
        )
    }

    // ==================== MEGA MILLIONS TESTS ====================

    @Test
    fun `generateNumbers for Mega Millions returns correct main number count`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.MEGA_MILLIONS)

        assertEquals(5, result.mainNumbers.size)
    }

    @Test
    fun `generateNumbers for Mega Millions returns correct bonus number count`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.MEGA_MILLIONS)

        assertEquals(1, result.bonusNumbers.size)
    }

    @Test
    fun `generateNumbers for Mega Millions main numbers are within valid range 1-70`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.MEGA_MILLIONS)

        assertTrue(
            "All main numbers should be between 1 and 70",
            result.mainNumbers.all { it in 1..70 }
        )
    }

    @Test
    fun `generateNumbers for Mega Millions bonus number is within valid range 1-25`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.MEGA_MILLIONS)

        assertTrue(
            "Bonus number should be between 1 and 25",
            result.bonusNumbers.all { it in 1..25 }
        )
    }

    // ==================== LOTTO 6/49 TESTS ====================

    @Test
    fun `generateNumbers for Lotto 6-49 returns correct main number count`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.LOTTO_6_49)

        assertEquals(6, result.mainNumbers.size)
    }

    @Test
    fun `generateNumbers for Lotto 6-49 returns no bonus numbers`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.LOTTO_6_49)

        assertTrue("Lotto 6/49 should have no bonus numbers", result.bonusNumbers.isEmpty())
    }

    @Test
    fun `generateNumbers for Lotto 6-49 main numbers are within valid range 1-49`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.LOTTO_6_49)

        assertTrue(
            "All main numbers should be between 1 and 49",
            result.mainNumbers.all { it in 1..49 }
        )
    }

    // ==================== UNIQUENESS TESTS ====================

    @Test
    fun `generateNumbers returns unique main numbers`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.POWERBALL)

        assertEquals(
            "Main numbers should all be unique",
            result.mainNumbers.size,
            result.mainNumbers.toSet().size
        )
    }

    @Test
    fun `generateNumbers returns unique main numbers for multiple generations`() = runTest {
        // Run multiple times to increase confidence in uniqueness
        repeat(100) {
            val result = api.generateNumbers(LotteryTypesData.POWERBALL)
            assertEquals(
                "Main numbers should all be unique (iteration $it)",
                result.mainNumbers.size,
                result.mainNumbers.toSet().size
            )
        }
    }

    @Test
    fun `generateNumbers returns unique bonus numbers`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.POWERBALL)

        assertEquals(
            "Bonus numbers should all be unique",
            result.bonusNumbers.size,
            result.bonusNumbers.toSet().size
        )
    }

    // ==================== SORTING TESTS ====================

    @Test
    fun `generateNumbers returns sorted main numbers`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.POWERBALL)

        assertEquals(
            "Main numbers should be sorted in ascending order",
            result.mainNumbers,
            result.mainNumbers.sorted()
        )
    }

    @Test
    fun `generateNumbers returns sorted bonus numbers`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.POWERBALL)

        assertEquals(
            "Bonus numbers should be sorted in ascending order",
            result.bonusNumbers,
            result.bonusNumbers.sorted()
        )
    }

    @Test
    fun `generateNumbers returns sorted numbers for non-Pick lottery types`() = runTest {
        // Pick games don't sort their numbers (they maintain draw order)
        val nonPickTypes = LotteryTypesData.ALL_TYPES.filter { !it.id.startsWith("pick_") }
        nonPickTypes.forEach { lotteryType ->
            val result = api.generateNumbers(lotteryType)

            assertEquals(
                "Main numbers should be sorted for ${lotteryType.name}",
                result.mainNumbers,
                result.mainNumbers.sorted()
            )
            assertEquals(
                "Bonus numbers should be sorted for ${lotteryType.name}",
                result.bonusNumbers,
                result.bonusNumbers.sorted()
            )
        }
    }

    // ==================== RANDOMNESS TESTS ====================

    @Test
    fun `generateNumbers produces different results on consecutive calls`() = runTest {
        val results = (1..10).map { api.generateNumbers(LotteryTypesData.POWERBALL) }
        val uniqueMainNumberSets = results.map { it.mainNumbers.toSet() }.toSet()

        // With 10 generations, we should have at least 2 different sets
        // (probability of all 10 being identical is astronomically low)
        assertTrue(
            "Multiple generations should produce different number sets",
            uniqueMainNumberSets.size > 1
        )
    }

    @Test
    fun `generateNumbers returns unique IDs for each generation`() = runTest {
        val results = (1..10).map { api.generateNumbers(LotteryTypesData.POWERBALL) }
        val uniqueIds = results.map { it.id }.toSet()

        assertEquals(
            "Each generation should have a unique ID",
            10,
            uniqueIds.size
        )
    }

    // ==================== METADATA TESTS ====================

    @Test
    fun `generateNumbers returns non-null ID`() = runTest {
        val result = api.generateNumbers(LotteryTypesData.POWERBALL)

        assertNotNull("Generated numbers should have an ID", result.id)
        assertTrue("ID should not be empty", result.id.isNotEmpty())
    }

    @Test
    fun `generateNumbers returns correct lottery type`() = runTest {
        val lotteryType = LotteryTypesData.MEGA_MILLIONS
        val result = api.generateNumbers(lotteryType)

        assertEquals(
            "Result should contain the requested lottery type",
            lotteryType,
            result.lotteryType
        )
    }

    @Test
    fun `generateNumbers returns valid timestamp`() = runTest {
        val beforeTime = System.currentTimeMillis()
        val result = api.generateNumbers(LotteryTypesData.POWERBALL)
        val afterTime = System.currentTimeMillis()

        assertTrue(
            "Timestamp should be between test start and end times",
            result.timestamp in beforeTime..afterTime
        )
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    fun `generateNumbers handles lottery type with no bonus numbers`() = runTest {
        val noBonusType = LotteryTypeDto(
            id = "test_no_bonus",
            name = "Test No Bonus",
            displayName = "Test No Bonus",
            mainNumberCount = 5,
            mainNumberMax = 50,
            bonusNumberCount = 0,
            bonusNumberMax = 0
        )

        val result = api.generateNumbers(noBonusType)

        assertEquals(5, result.mainNumbers.size)
        assertTrue("Should have no bonus numbers", result.bonusNumbers.isEmpty())
    }

    @Test
    fun `generateNumbers handles single main number`() = runTest {
        val singleNumberType = LotteryTypeDto(
            id = "test_single",
            name = "Test Single",
            displayName = "Test Single",
            mainNumberCount = 1,
            mainNumberMax = 10,
            bonusNumberCount = 0,
            bonusNumberMax = 0
        )

        val result = api.generateNumbers(singleNumberType)

        assertEquals(1, result.mainNumbers.size)
        assertTrue("Number should be between 1 and 10", result.mainNumbers[0] in 1..10)
    }

    @Test
    fun `generateNumbers handles maximum main numbers equal to max value`() = runTest {
        // Edge case: requesting all possible numbers (count == max)
        val allNumbersType = LotteryTypeDto(
            id = "test_all",
            name = "Test All",
            displayName = "Test All",
            mainNumberCount = 5,
            mainNumberMax = 5,
            bonusNumberCount = 0,
            bonusNumberMax = 0
        )

        val result = api.generateNumbers(allNumbersType)

        assertEquals(5, result.mainNumbers.size)
        assertEquals(
            "Should contain all numbers from 1 to 5",
            listOf(1, 2, 3, 4, 5),
            result.mainNumbers
        )
    }

    // ==================== LOTTERY TYPES API TESTS ====================

    @Test
    fun `getLotteryTypes returns all predefined lottery types`() = runTest {
        val result = api.getLotteryTypes()

        assertEquals(8, result.size)
    }

    @Test
    fun `getLotteryTypes includes Powerball`() = runTest {
        val result = api.getLotteryTypes()

        assertTrue(
            "Should include Powerball",
            result.any { it.id == "powerball" }
        )
    }

    @Test
    fun `getLotteryTypes includes Mega Millions`() = runTest {
        val result = api.getLotteryTypes()

        assertTrue(
            "Should include Mega Millions",
            result.any { it.id == "mega_millions" }
        )
    }

    @Test
    fun `getLotteryTypes includes Lotto 6-49`() = runTest {
        val result = api.getLotteryTypes()

        assertTrue(
            "Should include Lotto 6/49",
            result.any { it.id == "lotto_6_49" }
        )
    }

    // ==================== BOUNDARY TESTS ====================

    @Test
    fun `generateNumbers main numbers never include zero`() = runTest {
        repeat(50) {
            val result = api.generateNumbers(LotteryTypesData.POWERBALL)
            assertTrue(
                "No main number should be zero",
                result.mainNumbers.none { it == 0 }
            )
        }
    }

    @Test
    fun `generateNumbers bonus numbers never include zero`() = runTest {
        repeat(50) {
            val result = api.generateNumbers(LotteryTypesData.POWERBALL)
            assertTrue(
                "No bonus number should be zero",
                result.bonusNumbers.none { it == 0 }
            )
        }
    }

    @Test
    fun `generateNumbers main numbers never exceed maximum`() = runTest {
        repeat(50) {
            val result = api.generateNumbers(LotteryTypesData.POWERBALL)
            assertTrue(
                "No main number should exceed 69 for Powerball",
                result.mainNumbers.none { it > 69 }
            )
        }
    }

    @Test
    fun `generateNumbers bonus numbers never exceed maximum`() = runTest {
        repeat(50) {
            val result = api.generateNumbers(LotteryTypesData.POWERBALL)
            assertTrue(
                "No bonus number should exceed 26 for Powerball",
                result.bonusNumbers.none { it > 26 }
            )
        }
    }

    // ==================== CONSISTENCY TESTS ====================

    @Test
    fun `generateNumbers consistently returns correct structure for all lottery types`() = runTest {
        LotteryTypesData.ALL_TYPES.forEach { lotteryType ->
            repeat(10) {
                val result = api.generateNumbers(lotteryType)
                val isPickGame = lotteryType.id.startsWith("pick_")
                val minNumber = if (isPickGame) 0 else 1

                assertEquals(
                    "Main number count mismatch for ${lotteryType.name}",
                    lotteryType.mainNumberCount,
                    result.mainNumbers.size
                )
                assertEquals(
                    "Bonus number count mismatch for ${lotteryType.name}",
                    lotteryType.bonusNumberCount,
                    result.bonusNumbers.size
                )
                assertTrue(
                    "Main numbers out of range for ${lotteryType.name}",
                    result.mainNumbers.all { it in minNumber..lotteryType.mainNumberMax }
                )
                if (lotteryType.bonusNumberCount > 0) {
                    assertTrue(
                        "Bonus numbers out of range for ${lotteryType.name}",
                        result.bonusNumbers.all { it in 1..lotteryType.bonusNumberMax }
                    )
                }
            }
        }
    }
}
