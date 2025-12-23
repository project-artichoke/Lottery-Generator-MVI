package com.aaltix.lotto.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import com.aaltix.lotto.core.navigation.BottomNavItem
import com.aaltix.lotto.core.ui.adaptive.isTablet

/**
 * Adaptive scaffold that shows NavigationRail on tablets
 * and BottomNavigationBar on phones.
 *
 * On tablets (medium and expanded width classes):
 * - Shows NavigationRail on the left side
 * - Content fills remaining space to the right
 *
 * On phones (compact width class):
 * - Shows BottomNavigationBar at the bottom
 * - Standard Scaffold layout
 *
 * @param currentDestination The current navigation destination for highlighting
 * @param showNavigation Whether to show navigation (bottom bar or rail)
 * @param onNavItemClick Callback when a navigation item is clicked
 * @param content The main content to display
 */
@Composable
fun AdaptiveScaffold(
    currentDestination: NavDestination?,
    showNavigation: Boolean,
    onNavItemClick: (BottomNavItem) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    if (isTablet() && showNavigation) {
        // Tablet layout with NavigationRail on the left
        Row(modifier = Modifier.fillMaxSize()) {
            LottoNavigationRail(
                currentDestination = currentDestination,
                onItemClick = onNavItemClick
            )
            content(Modifier.weight(1f))
        }
    } else {
        // Phone layout with BottomNavigationBar
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showNavigation) {
                    LottoBottomNavigationBar(
                        currentDestination = currentDestination,
                        onItemClick = onNavItemClick
                    )
                }
            }
        ) { paddingValues ->
            content(Modifier.padding(paddingValues))
        }
    }
}
