package com.aaltix.lotto.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation items for the app.
 */
enum class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    Generator(
        route = NavRoutes.GENERATOR,
        icon = Icons.Default.Stars,
        label = "Generate"
    ),
    History(
        route = NavRoutes.HISTORY,
        icon = Icons.Default.History,
        label = "History"
    ),
    Settings(
        route = NavRoutes.SETTINGS,
        icon = Icons.Default.Settings,
        label = "Settings"
    )
}
