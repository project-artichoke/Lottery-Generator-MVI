package com.aaltix.lotto.di

import com.aaltix.lotto.core.common.di.commonModule
import com.aaltix.lotto.core.data.di.dataModule
import com.aaltix.lotto.core.database.di.databaseModule
import com.aaltix.lotto.core.domain.di.domainModule
import com.aaltix.lotto.feature.generator.di.generatorModule
import com.aaltix.lotto.feature.history.di.historyModule
import com.aaltix.lotto.feature.settings.di.settingsModule
import org.koin.dsl.module

/**
 * Main Koin module that includes all feature and core modules.
 */
val appModule = module {
    includes(
        // Core modules
        commonModule,
        databaseModule,
        dataModule,
        domainModule,

        // Feature modules
        generatorModule,
        historyModule,
        settingsModule
    )
}
