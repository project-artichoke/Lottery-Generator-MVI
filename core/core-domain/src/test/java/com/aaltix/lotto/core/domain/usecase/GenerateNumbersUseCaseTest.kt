package com.aaltix.lotto.core.domain.usecase

import com.aaltix.lotto.core.common.result.Result
import com.aaltix.lotto.core.domain.fake.FakeLotteryRepository
import com.aaltix.lotto.core.domain.fake.TestLotteryTypes
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
class GenerateNumbersUseCaseTest {

    private lateinit var useCase: GenerateNumbersUseCase
    private lateinit var fakeRepository: FakeLotteryRepository
    private lateinit var dispatcherProvider: TestDispatcherProvider
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        fakeRepository = FakeLotteryRepository()
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
        useCase = GenerateNumbersUseCase(fakeRepository, dispatcherProvider)
    }

    @Test
    fun `invoke returns success with generated numbers for Powerball`() = runTest(testDispatcher) {
        // Given
        val lotteryType = TestLotteryTypes.POWERBALL

        // When
        val result = useCase(lotteryType)

        // Then
        assertTrue(result is Result.Success)
        val numbers = (result as Result.Success).data
        assertNotNull(numbers.id)
        assertEquals(5, numbers.mainNumbers.size)
        assertEquals(1, numbers.bonusNumbers.size)
        assertTrue(numbers.mainNumbers.all { it in 1..69 })
        assertTrue(numbers.bonusNumbers.all { it in 1..26 })
    }

    @Test
    fun `invoke returns success with generated numbers for Mega Millions`() = runTest(testDispatcher) {
        // Given
        val lotteryType = TestLotteryTypes.MEGA_MILLIONS

        // When
        val result = useCase(lotteryType)

        // Then
        assertTrue(result is Result.Success)
        val numbers = (result as Result.Success).data
        assertEquals(5, numbers.mainNumbers.size)
        assertEquals(1, numbers.bonusNumbers.size)
        assertTrue(numbers.mainNumbers.all { it in 1..70 })
        assertTrue(numbers.bonusNumbers.all { it in 1..25 })
    }

    @Test
    fun `invoke returns success with generated numbers for Lotto 6-49`() = runTest(testDispatcher) {
        // Given
        val lotteryType = TestLotteryTypes.LOTTO_649

        // When
        val result = useCase(lotteryType)

        // Then
        assertTrue(result is Result.Success)
        val numbers = (result as Result.Success).data
        assertEquals(6, numbers.mainNumbers.size)
        assertTrue(numbers.bonusNumbers.isEmpty())
        assertTrue(numbers.mainNumbers.all { it in 1..49 })
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest(testDispatcher) {
        // Given
        fakeRepository.shouldThrowOnGenerate = true
        val lotteryType = TestLotteryTypes.POWERBALL

        // When
        val result = useCase(lotteryType)

        // Then
        assertTrue(result is Result.Error)
    }

    @Test
    fun `generated numbers are sorted`() = runTest(testDispatcher) {
        // Given
        val lotteryType = TestLotteryTypes.POWERBALL

        // When
        val result = useCase(lotteryType)

        // Then
        assertTrue(result is Result.Success)
        val numbers = (result as Result.Success).data.mainNumbers
        assertEquals(numbers, numbers.sorted())
    }
}
