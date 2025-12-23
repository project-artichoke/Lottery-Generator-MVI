package com.aaltix.lotto.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.aaltix.lotto.core.navigation.BottomNavItem
import com.aaltix.lotto.core.ui.theme.SkyBlue
import com.aaltix.lotto.core.ui.theme.SkyBlueDark

/**
 * Navigation rail for tablet layouts.
 * Displays navigation items vertically on the left side with sky blue theme.
 * Used on medium (7") and expanded (10"+) tablets.
 *
 * @param currentDestination The current navigation destination
 * @param onItemClick Callback when a navigation item is clicked
 */
@Composable
fun LottoNavigationRail(
    currentDestination: NavDestination?,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationRail(
        containerColor = SkyBlue,
        header = {
            Spacer(modifier = Modifier.height(12.dp))
        }
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        BottomNavItem.entries.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == item.route
            } == true

            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selected,
                onClick = { onItemClick(item) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = SkyBlueDark,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White.copy(alpha = 0.7f)
                )
            )
        }
    }
}
