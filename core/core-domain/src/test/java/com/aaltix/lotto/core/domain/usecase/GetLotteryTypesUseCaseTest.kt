package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.fake.FakeCustomLotteryTypeRepository
import com.aaltix.lotto.core.domain.fake.FakeLotteryRepository
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.util.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetLotteryTypesUseCaseTest {

    private lateinit var useCase: GetLotteryTypesUseCase
    private lateinit var fakeRepository: FakeLotteryRepository
    private lateinit var fakeCustomRepository: FakeCustomLotteryTypeRepository
    private lateinit var dispatcherProvider: TestDispatcherProvider
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        fakeRepository = FakeLotteryRepository()
        fakeCustomRepository = FakeCustomLotteryTypeRepository()
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
        useCase = GetLotteryTypesUseCase(fakeRepository, fakeCustomRepository, dispatcherProvider)
    }

    @Test
    fun `invoke returns success with lottery types`() = runTest(testDispatcher) {
        // Given
        val customType = LotteryType(
            id = "custom_pick3",
            name = "Pick 3",
            displayName = "Custom Pick 3",
            mainNumberCount = 3,
            mainNumberMax = 30,
            bonusNumberCount = 0,
            bonusNumberMax = 0,
            isCustom = true
        )
        fakeCustomRepository.setCustomTypes(listOf(customType))
        val expectedCount = fakeRepository.predefinedTypes.size + 1 // predefined + 1 custom

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Success)
        val types = (result as Result.Success).data
        assertEquals(expectedCount, types.size)
    }

    @Test
    fun `invoke returns Powerball, Mega Millions, and Lotto 6-49`() = runTest(testDispatcher) {
        // Given - no additional setup

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Success)
        val types = (result as Result.Success).data
        val ids = types.map { it.id }
        assertTrue("powerball" in ids)
        assertTrue("mega_millions" in ids)
        assertTrue("lotto_649" in ids)
    }

    @Test
    fun `Powerball has correct configuration`() = runTest(testDispatcher) {
        // Given - no additional setup

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Success)
        val powerball = (result as Result.Success).data.find { it.id == "powerball" }
        assertNotNull(powerball)
        assertEquals("Powerball", powerball!!.name)
        assertEquals(5, powerball.mainNumberCount)
        assertEquals(69, powerball.mainNumberMax)
        assertTrue(powerball.hasBonusNumbers)
        assertEquals(26, powerball.bonusNumberMax)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest(testDispatcher) {
        // Given
        fakeRepository.shouldThrowOnGetTypes = true

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Error)
    }
}
