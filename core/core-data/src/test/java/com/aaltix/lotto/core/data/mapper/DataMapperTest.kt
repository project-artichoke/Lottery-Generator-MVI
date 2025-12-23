package com.aaltix.lotto.core.data.mapper

import com.aaltix.lotto.core.data.mapper.DataMapper.toDto
import com.aaltix.lotto.core.data.mapper.DataMapper.toEntity
import com.aaltix.lotto.core.data.model.GeneratedNumbersDto
import com.aaltix.lotto.core.data.model.LotteryTypesData
import com.aaltix.lotto.core.database.entity.HistoryEntryEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for DataMapper.
 * Tests cover:
 * - DTO to Entity conversion
 * - Entity to DTO conversion
 * - Safe number parsing
 * - Edge cases and error handling
 */
class DataMapperTest {

    // Helper function to create HistoryEntryEntity with Powerball defaults
    private fun createPowerballEntity(
        id: String = "test-id",
        mainNumbers: String = "5,12,23,44,67",
        bonusNumbers: String = "15",
        timestamp: Long = 1234567890L
    ) = HistoryEntryEntity(
        id = id,
        lotteryTypeId = "powerball",
        lotteryTypeName = "Powerball",
        lotteryTypeMainNumberCount = 5,
        lotteryTypeMainNumberMax = 69,
        lotteryTypeBonusNumberCount = 1,
        lotteryTypeBonusNumberMax = 26,
        isCustomLotteryType = false,
        mainNumbers = mainNumbers,
        bonusNumbers = bonusNumbers,
        timestamp = timestamp
    )

    private fun createLotto649Entity(
        id: String = "test-id",
        mainNumbers: String = "1,2,3,4,5,6",
        bonusNumbers: String = "",
        timestamp: Long = 1234567890L
    ) = HistoryEntryEntity(
        id = id,
        lotteryTypeId = "lotto_6_49",
        lotteryTypeName = "Lotto 6/49",
        lotteryTypeMainNumberCount = 6,
        lotteryTypeMainNumberMax = 49,
        lotteryTypeBonusNumberCount = 0,
        lotteryTypeBonusNumberMax = 0,
        isCustomLotteryType = false,
        mainNumbers = mainNumbers,
        bonusNumbers = bonusNumbers,
        timestamp = timestamp
    )

    private fun createMegaMillionsEntity(
        id: String = "test-id",
        mainNumbers: String = "1,2,3,4,5",
        bonusNumbers: String = "10",
        timestamp: Long = 1234567890L
    ) = HistoryEntryEntity(
        id = id,
        lotteryTypeId = "mega_millions",
        lotteryTypeName = "Mega Millions",
        lotteryTypeMainNumberCount = 5,
        lotteryTypeMainNumberMax = 70,
        lotteryTypeBonusNumberCount = 1,
        lotteryTypeBonusNumberMax = 25,
        isCustomLotteryType = false,
        mainNumbers = mainNumbers,
        bonusNumbers = bonusNumbers,
        timestamp = timestamp
    )

    private fun createUnknownEntity(
        id: String = "test-id",
        mainNumbers: String = "1,2,3,4,5",
        bonusNumbers: String = "10",
        timestamp: Long = 1234567890L
    ) = HistoryEntryEntity(
        id = id,
        lotteryTypeId = "unknown_type",
        lotteryTypeName = "Unknown",
        lotteryTypeMainNumberCount = 5,
        lotteryTypeMainNumberMax = 69,
        lotteryTypeBonusNumberCount = 1,
        lotteryTypeBonusNumberMax = 26,
        isCustomLotteryType = false,
        mainNumbers = mainNumbers,
        bonusNumbers = bonusNumbers,
        timestamp = timestamp
    )

    // ==================== DTO TO ENTITY TESTS ====================

    @Test
    fun `toEntity converts GeneratedNumbersDto to HistoryEntryEntity correctly`() {
        val dto = GeneratedNumbersDto(
            id = "test-id",
            lotteryType = LotteryTypesData.POWERBALL,
            mainNumbers = listOf(5, 12, 23, 44, 67),
            bonusNumbers = listOf(15),
            timestamp = 1234567890L
        )

        val entity = dto.toEntity()

        assertEquals("test-id", entity.id)
        assertEquals("powerball", entity.lotteryTypeId)
        assertEquals("Powerball", entity.lotteryTypeName)
        assertEquals("5,12,23,44,67", entity.mainNumbers)
        assertEquals("15", entity.bonusNumbers)
        assertEquals(1234567890L, entity.timestamp)
    }

    @Test
    fun `toEntity handles empty bonus numbers`() {
        val dto = GeneratedNumbersDto(
            id = "test-id",
            lotteryType = LotteryTypesData.LOTTO_6_49,
            mainNumbers = listOf(1, 2, 3, 4, 5, 6),
            bonusNumbers = emptyList(),
            timestamp = 1234567890L
        )

        val entity = dto.toEntity()

        assertEquals("", entity.bonusNumbers)
    }

    @Test
    fun `toEntity preserves number order in string representation`() {
        val dto = GeneratedNumbersDto(
            id = "test-id",
            lotteryType = LotteryTypesData.POWERBALL,
            mainNumbers = listOf(1, 10, 20, 30, 40),
            bonusNumbers = listOf(5),
            timestamp = 1234567890L
        )

        val entity = dto.toEntity()

        assertEquals("1,10,20,30,40", entity.mainNumbers)
    }

    // ==================== ENTITY TO DTO TESTS ====================

    @Test
    fun `toDto converts HistoryEntryEntity to GeneratedNumbersDto correctly`() {
        val entity = createPowerballEntity()

        val dto = entity.toDto()

        assertEquals("test-id", dto.id)
        assertEquals(LotteryTypesData.POWERBALL, dto.lotteryType)
        assertEquals(listOf(5, 12, 23, 44, 67), dto.mainNumbers)
        assertEquals(listOf(15), dto.bonusNumbers)
        assertEquals(1234567890L, dto.timestamp)
    }

    @Test
    fun `toDto handles empty bonus numbers string`() {
        val entity = createLotto649Entity()

        val dto = entity.toDto()

        assertTrue("Bonus numbers should be empty", dto.bonusNumbers.isEmpty())
    }

    @Test
    fun `toDto handles blank bonus numbers string`() {
        val entity = createLotto649Entity(bonusNumbers = "   ")

        val dto = entity.toDto()

        assertTrue("Bonus numbers should be empty for blank string", dto.bonusNumbers.isEmpty())
    }

    @Test
    fun `toDto reconstructs unknown lottery type from stored entity values`() {
        val entity = createUnknownEntity()

        val dto = entity.toDto()

        // Unknown types are reconstructed from the entity's stored snapshot values
        assertEquals("unknown_type", dto.lotteryType.id)
        assertEquals("Unknown", dto.lotteryType.name)
        assertEquals(5, dto.lotteryType.mainNumberCount)
        assertEquals(69, dto.lotteryType.mainNumberMax)
        assertEquals(1, dto.lotteryType.bonusNumberCount)
        assertEquals(26, dto.lotteryType.bonusNumberMax)
    }

    @Test
    fun `toDto correctly maps Mega Millions lottery type`() {
        val entity = createMegaMillionsEntity()

        val dto = entity.toDto()

        assertEquals(LotteryTypesData.MEGA_MILLIONS, dto.lotteryType)
    }

    @Test
    fun `toDto correctly maps Lotto 6-49 lottery type`() {
        val entity = createLotto649Entity()

        val dto = entity.toDto()

        assertEquals(LotteryTypesData.LOTTO_6_49, dto.lotteryType)
    }

    // ==================== SAFE PARSING TESTS ====================

    @Test
    fun `toDto safely parses numbers with extra whitespace`() {
        val entity = createPowerballEntity(
            mainNumbers = " 5 , 12 , 23 , 44 , 67 ",
            bonusNumbers = " 15 "
        )

        val dto = entity.toDto()

        assertEquals(listOf(5, 12, 23, 44, 67), dto.mainNumbers)
        assertEquals(listOf(15), dto.bonusNumbers)
    }

    @Test
    fun `toDto skips invalid number values gracefully`() {
        val entity = createPowerballEntity(mainNumbers = "5,abc,23,xyz,67")

        val dto = entity.toDto()

        // Should only contain the valid numbers
        assertEquals(listOf(5, 23, 67), dto.mainNumbers)
    }

    @Test
    fun `toDto handles completely invalid main numbers string`() {
        val entity = createPowerballEntity(mainNumbers = "abc,xyz,!!")

        val dto = entity.toDto()

        assertTrue("Main numbers should be empty for all invalid values", dto.mainNumbers.isEmpty())
    }

    @Test
    fun `toDto handles empty main numbers string`() {
        val entity = createPowerballEntity(mainNumbers = "")

        val dto = entity.toDto()

        assertTrue("Main numbers should be empty", dto.mainNumbers.isEmpty())
    }

    @Test
    fun `toDto handles trailing comma in numbers string`() {
        val entity = createPowerballEntity(
            mainNumbers = "5,12,23,44,67,",
            bonusNumbers = "15,"
        )

        val dto = entity.toDto()

        assertEquals(listOf(5, 12, 23, 44, 67), dto.mainNumbers)
        assertEquals(listOf(15), dto.bonusNumbers)
    }

    @Test
    fun `toDto handles leading comma in numbers string`() {
        val entity = createPowerballEntity(
            mainNumbers = ",5,12,23,44,67",
            bonusNumbers = ",15"
        )

        val dto = entity.toDto()

        assertEquals(listOf(5, 12, 23, 44, 67), dto.mainNumbers)
        assertEquals(listOf(15), dto.bonusNumbers)
    }

    @Test
    fun `toDto handles multiple consecutive commas`() {
        val entity = createPowerballEntity(mainNumbers = "5,,12,,,23")

        val dto = entity.toDto()

        assertEquals(listOf(5, 12, 23), dto.mainNumbers)
    }

    // ==================== ROUND-TRIP TESTS ====================

    @Test
    fun `round-trip conversion preserves data for Powerball`() {
        val originalDto = GeneratedNumbersDto(
            id = "round-trip-test",
            lotteryType = LotteryTypesData.POWERBALL,
            mainNumbers = listOf(5, 12, 23, 44, 67),
            bonusNumbers = listOf(15),
            timestamp = 1234567890L
        )

        val entity = originalDto.toEntity()
        val convertedDto = entity.toDto()

        assertEquals(originalDto.id, convertedDto.id)
        assertEquals(originalDto.lotteryType, convertedDto.lotteryType)
        assertEquals(originalDto.mainNumbers, convertedDto.mainNumbers)
        assertEquals(originalDto.bonusNumbers, convertedDto.bonusNumbers)
        assertEquals(originalDto.timestamp, convertedDto.timestamp)
    }

    @Test
    fun `round-trip conversion preserves data for Mega Millions`() {
        val originalDto = GeneratedNumbersDto(
            id = "round-trip-test",
            lotteryType = LotteryTypesData.MEGA_MILLIONS,
            mainNumbers = listOf(10, 20, 30, 40, 50),
            bonusNumbers = listOf(5),
            timestamp = 9876543210L
        )

        val entity = originalDto.toEntity()
        val convertedDto = entity.toDto()

        assertEquals(originalDto.id, convertedDto.id)
        assertEquals(originalDto.lotteryType, convertedDto.lotteryType)
        assertEquals(originalDto.mainNumbers, convertedDto.mainNumbers)
        assertEquals(originalDto.bonusNumbers, convertedDto.bonusNumbers)
        assertEquals(originalDto.timestamp, convertedDto.timestamp)
    }

    @Test
    fun `round-trip conversion preserves data for Lotto 6-49 without bonus numbers`() {
        val originalDto = GeneratedNumbersDto(
            id = "round-trip-test",
            lotteryType = LotteryTypesData.LOTTO_6_49,
            mainNumbers = listOf(1, 12, 23, 34, 45, 49),
            bonusNumbers = emptyList(),
            timestamp = 5555555555L
        )

        val entity = originalDto.toEntity()
        val convertedDto = entity.toDto()

        assertEquals(originalDto.id, convertedDto.id)
        assertEquals(originalDto.lotteryType, convertedDto.lotteryType)
        assertEquals(originalDto.mainNumbers, convertedDto.mainNumbers)
        assertEquals(originalDto.bonusNumbers, convertedDto.bonusNumbers)
        assertEquals(originalDto.timestamp, convertedDto.timestamp)
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    fun `toDto handles single digit numbers`() {
        val entity = createPowerballEntity(
            mainNumbers = "1,2,3,4,5",
            bonusNumbers = "1"
        )

        val dto = entity.toDto()

        assertEquals(listOf(1, 2, 3, 4, 5), dto.mainNumbers)
        assertEquals(listOf(1), dto.bonusNumbers)
    }

    @Test
    fun `toDto handles large numbers`() {
        val entity = createPowerballEntity(
            mainNumbers = "65,66,67,68,69",
            bonusNumbers = "26"
        )

        val dto = entity.toDto()

        assertEquals(listOf(65, 66, 67, 68, 69), dto.mainNumbers)
        assertEquals(listOf(26), dto.bonusNumbers)
    }

    @Test
    fun `toEntity handles single number in list`() {
        val dto = GeneratedNumbersDto(
            id = "test-id",
            lotteryType = LotteryTypesData.POWERBALL,
            mainNumbers = listOf(42),
            bonusNumbers = listOf(7),
            timestamp = 1234567890L
        )

        val entity = dto.toEntity()

        assertEquals("42", entity.mainNumbers)
        assertEquals("7", entity.bonusNumbers)
    }
}
