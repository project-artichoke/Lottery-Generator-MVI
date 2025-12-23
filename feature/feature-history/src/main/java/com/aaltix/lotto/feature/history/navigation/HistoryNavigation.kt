package com.aaltix.lotto.feature.history.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aaltix.lotto.core.navigation.NavRoutes
import com.aaltix.lotto.feature.history.presentation.detail.HistoryDetailScreen
import com.aaltix.lotto.feature.history.presentation.list.HistoryListScreen

/**
 * Navigation extension for the History feature.
 */
fun NavGraphBuilder.historyNavGraph(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    composable(NavRoutes.HISTORY) {
        HistoryListScreen(
            onNavigateToDetail = onNavigateToDetail
        )
    }

    composable(
        route = NavRoutes.HISTORY_DETAIL,
        arguments = listOf(
            navArgument("entryId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val entryId = backStackEntry.arguments?.getString("entryId") ?: return@composable
        HistoryDetailScreen(
            entryId = entryId,
            onNavigateBack = onNavigateBack
        )
    }
}
