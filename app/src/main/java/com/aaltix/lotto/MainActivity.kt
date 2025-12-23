package com.aaltix.lotto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import com.aaltix.lotto.core.ui.adaptive.LocalWindowSizeClass
import com.aaltix.lotto.core.ui.theme.LottoTheme
import com.aaltix.lotto.ui.MainScreen

/**
 * Main activity for the Lottery Generator app.
 * Uses single-activity architecture with Jetpack Compose.
 * Provides WindowSizeClass for adaptive tablet layouts.
 */
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            CompositionLocalProvider(
                LocalWindowSizeClass provides windowSizeClass
            ) {
                LottoTheme {
                    MainScreen()
                }
            }
        }
    }
}
