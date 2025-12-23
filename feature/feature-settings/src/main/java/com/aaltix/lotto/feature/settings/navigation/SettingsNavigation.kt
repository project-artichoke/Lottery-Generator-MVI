package com.aaltix.lotto.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aaltix.lotto.core.navigation.NavRoutes
import com.aaltix.lotto.feature.settings.presentation.SettingsScreen
import com.aaltix.lotto.feature.settings.presentation.addedit.AddEditLotteryTypeScreen
import com.aaltix.lotto.feature.settings.presentation.customtypes.CustomTypesScreen

/**
 * Navigation extension for the Settings feature.
 */
fun NavGraphBuilder.settingsNavGraph(
    onNavigateToCustomTypes: () -> Unit,
    onNavigateToAddType: () -> Unit,
    onNavigateToEditType: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    composable(NavRoutes.SETTINGS) {
        SettingsScreen(
            onNavigateToCustomTypes = onNavigateToCustomTypes
        )
    }

    composable(NavRoutes.CUSTOM_LOTTERY_TYPES) {
        CustomTypesScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToAdd = onNavigateToAddType,
            onNavigateToEdit = onNavigateToEditType
        )
    }

    composable(NavRoutes.ADD_LOTTERY_TYPE) {
        AddEditLotteryTypeScreen(
            typeId = null,
            onNavigateBack = onNavigateBack
        )
    }

    composable(
        route = NavRoutes.EDIT_LOTTERY_TYPE,
        arguments = listOf(
            navArgument("typeId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val typeId = backStackEntry.arguments?.getString("typeId")
        AddEditLotteryTypeScreen(
            typeId = typeId,
            onNavigateBack = onNavigateBack
        )
    }
}
