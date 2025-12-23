package com.aaltix.lotto.feature.generator.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aaltix.lotto.core.navigation.NavRoutes
import com.aaltix.lotto.feature.generator.presentation.GeneratorScreen

/**
 * Navigation extension for the Generator feature.
 */
fun NavGraphBuilder.generatorNavGraph(
    onNavigateToHistory: () -> Unit
) {
    composable(NavRoutes.GENERATOR) {
        GeneratorScreen(
            onNavigateToHistory = onNavigateToHistory
        )
    }
}
