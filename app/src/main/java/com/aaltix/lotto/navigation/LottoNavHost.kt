package com.aaltix.lotto.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aaltix.lotto.core.navigation.NavRoutes
import com.aaltix.lotto.feature.generator.navigation.generatorNavGraph
import com.aaltix.lotto.feature.history.navigation.historyNavGraph
import com.aaltix.lotto.feature.settings.navigation.settingsNavGraph

/**
 * Navigation host for the app.
 */
@Composable
fun LottoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.GENERATOR,
        modifier = modifier
    ) {
        // Generator feature
        generatorNavGraph(
            onNavigateToHistory = {
                navController.navigate(NavRoutes.HISTORY) {
                    launchSingleTop = true
                }
            }
        )

        // History feature
        historyNavGraph(
            onNavigateToDetail = { entryId ->
                navController.navigate(NavRoutes.historyDetail(entryId))
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )

        // Settings feature
        settingsNavGraph(
            onNavigateToCustomTypes = {
                navController.navigate(NavRoutes.CUSTOM_LOTTERY_TYPES)
            },
            onNavigateToAddType = {
                navController.navigate(NavRoutes.ADD_LOTTERY_TYPE)
            },
            onNavigateToEditType = { typeId ->
                navController.navigate(NavRoutes.editLotteryType(typeId))
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}
