package com.aaltix.lotto.feature.history.di

import com.aaltix.lotto.feature.history.presentation.detail.HistoryDetailViewModel
import com.aaltix.lotto.feature.history.presentation.list.HistoryListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val historyModule = module {
    viewModelOf(::HistoryListViewModel)
    viewModelOf(::HistoryDetailViewModel)
}
