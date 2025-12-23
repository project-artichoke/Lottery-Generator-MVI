package com.aaltix.lotto.core.data.di

import com.aaltix.lotto.core.data.api.LotteryApi
import com.aaltix.lotto.core.data.api.StubbedLotteryApi
import com.aaltix.lotto.core.data.preferences.UserPreferencesRepositoryImpl
import com.aaltix.lotto.core.data.repository.CustomLotteryTypeRepositoryImpl
import com.aaltix.lotto.core.data.repository.LotteryRepositoryImpl
import com.aaltix.lotto.core.domain.repository.CustomLotteryTypeRepository
import com.aaltix.lotto.core.domain.repository.LotteryRepository
import com.aaltix.lotto.core.domain.repository.UserPreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<LotteryApi> { StubbedLotteryApi() }
    single<LotteryRepository> { LotteryRepositoryImpl(get(), get()) }
    single<CustomLotteryTypeRepository> { CustomLotteryTypeRepositoryImpl(get()) }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(androidContext()) }
}
