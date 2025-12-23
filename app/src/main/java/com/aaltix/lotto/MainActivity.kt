package com.aaltix.lotto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aaltix.lotto.core.ui.theme.LottoTheme
import com.aaltix.lotto.ui.MainScreen

/**
 * Main activity for the Lottery Generator app.
 * Uses single-activity architecture with Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LottoTheme {
                MainScreen()
            }
        }
    }
}
