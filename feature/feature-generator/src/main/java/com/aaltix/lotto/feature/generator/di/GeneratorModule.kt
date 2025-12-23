package com.aaltix.lotto.feature.generator.di

import com.aaltix.lotto.feature.generator.presentation.GeneratorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val generatorModule = module {
    viewModelOf(::GeneratorViewModel)
}
