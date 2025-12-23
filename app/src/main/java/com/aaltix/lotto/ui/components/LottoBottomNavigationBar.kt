package com.aaltix.lotto.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.aaltix.lotto.core.navigation.BottomNavItem
import com.aaltix.lotto.core.ui.theme.SkyBlue
import com.aaltix.lotto.core.ui.theme.SkyBlueDark

/**
 * Bottom navigation bar for the Lotto app.
 * Displays navigation items with sky blue theme.
 *
 * @param currentDestination The current navigation destination
 * @param onItemClick Callback when a navigation item is clicked
 */
@Composable
fun LottoBottomNavigationBar(
    currentDestination: NavDestination?,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = SkyBlue
    ) {
        BottomNavItem.entries.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == item.route
            } == true

            LottoNavigationBarItem(
                item = item,
                selected = selected,
                onClick = { onItemClick(item) }
            )
        }
    }
}

/**
 * Individual navigation bar item with custom styling.
 *
 * @param item The bottom navigation item to display
 * @param selected Whether this item is currently selected
 * @param onClick Callback when this item is clicked
 */
@Composable
private fun RowScope.LottoNavigationBarItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label
            )
        },
        label = { Text(item.label) },
        selected = selected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            indicatorColor = SkyBlueDark,
            unselectedIconColor = Color.White.copy(alpha = 0.7f),
            unselectedTextColor = Color.White.copy(alpha = 0.7f)
        )
    )
}
