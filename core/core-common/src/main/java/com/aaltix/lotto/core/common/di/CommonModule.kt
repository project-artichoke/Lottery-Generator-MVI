package com.aaltix.lotto.core.common.di

import com.aaltix.lotto.core.common.dispatchers.DefaultDispatcherProvider
import com.aaltix.lotto.core.common.dispatchers.DispatcherProvider
import org.koin.dsl.module

val commonModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
}
