package com.aaltix.lotto

import android.app.Application
import com.aaltix.lotto.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application class for the Lottery Generator app.
 * Initializes Koin dependency injection.
 */
class LottoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@LottoApplication)
            modules(appModule)
        }
    }
}
