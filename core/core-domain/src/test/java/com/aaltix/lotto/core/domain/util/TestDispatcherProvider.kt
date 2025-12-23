package com.aaltix.lotto.core.domain.util

import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

class TestDispatcherProvider(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : DispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher

    val dispatcher: TestDispatcher get() = testDispatcher
}
