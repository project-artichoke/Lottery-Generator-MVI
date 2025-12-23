package com.aaltix.lotto.core.domain.di

import com.aaltix.lotto.core.domain.usecase.CheckConfettiShownUseCase
import com.aaltix.lotto.core.domain.usecase.ClearHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.ClearLastGeneratedNumbersUseCase
import com.aaltix.lotto.core.domain.usecase.DeleteCustomLotteryTypeUseCase
import com.aaltix.lotto.core.domain.usecase.DeleteHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.GenerateNumbersUseCase
import com.aaltix.lotto.core.domain.usecase.GetCustomLotteryTypeByIdUseCase
import com.aaltix.lotto.core.domain.usecase.GetCustomLotteryTypesCountUseCase
import com.aaltix.lotto.core.domain.usecase.GetCustomLotteryTypesUseCase
import com.aaltix.lotto.core.domain.usecase.GetHistoryDetailUseCase
import com.aaltix.lotto.core.domain.usecase.GetHistoryUseCase
import com.aaltix.lotto.core.domain.usecase.GetLastGeneratedNumbersIdUseCase
import com.aaltix.lotto.core.domain.usecase.GetLotteryTypesUseCase
import com.aaltix.lotto.core.domain.usecase.GetSelectedLotteryTypeIdUseCase
import com.aaltix.lotto.core.domain.usecase.MarkConfettiShownUseCase
import com.aaltix.lotto.core.domain.usecase.SaveCustomLotteryTypeUseCase
import com.aaltix.lotto.core.domain.usecase.SaveLastGeneratedNumbersIdUseCase
import com.aaltix.lotto.core.domain.usecase.SaveSelectedLotteryTypeIdUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GenerateNumbersUseCase(get(), get()) }
    factory { GetLotteryTypesUseCase(get(), get(), get()) }
    factory { GetHistoryUseCase(get()) }
    factory { GetHistoryDetailUseCase(get(), get()) }
    factory { DeleteHistoryUseCase(get(), get()) }
    factory { ClearHistoryUseCase(get(), get()) }

    // Custom lottery type use cases
    factory { SaveCustomLotteryTypeUseCase(get(), get()) }
    factory { DeleteCustomLotteryTypeUseCase(get(), get()) }
    factory { GetCustomLotteryTypesUseCase(get()) }
    factory { GetCustomLotteryTypeByIdUseCase(get(), get()) }
    factory { GetCustomLotteryTypesCountUseCase(get()) }

    // User preferences use cases
    factory { GetSelectedLotteryTypeIdUseCase(get()) }
    factory { SaveSelectedLotteryTypeIdUseCase(get()) }
    factory { GetLastGeneratedNumbersIdUseCase(get()) }
    factory { SaveLastGeneratedNumbersIdUseCase(get()) }
    factory { ClearLastGeneratedNumbersUseCase(get()) }
    factory { CheckConfettiShownUseCase(get()) }
    factory { MarkConfettiShownUseCase(get()) }
}
