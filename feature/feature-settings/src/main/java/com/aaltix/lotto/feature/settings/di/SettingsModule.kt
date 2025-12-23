package com.aaltix.lotto.feature.settings.di

import com.aaltix.lotto.feature.settings.presentation.SettingsViewModel
import com.aaltix.lotto.feature.settings.presentation.addedit.AddEditLotteryTypeViewModel
import com.aaltix.lotto.feature.settings.presentation.customtypes.CustomTypesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
    viewModelOf(::CustomTypesViewModel)
    viewModelOf(::AddEditLotteryTypeViewModel)
}
