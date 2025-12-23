package com.aaltix.lotto.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aaltix.lotto.core.domain.repository.UserPreferencesRepository
import com.aaltix.lotto.core.navigation.BottomNavItem
import com.aaltix.lotto.navigation.LottoNavHost
import com.aaltix.lotto.ui.components.LottoBottomNavigationBar
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Main screen with bottom navigation.
 * Acts as the container for the app's navigation structure.
 * Shows disclaimer on first launch before allowing app access.
 */
@Composable
fun MainScreen(
    userPreferencesRepository: UserPreferencesRepository = koinInject()
) {
    val hasAcceptedDisclaimer by userPreferencesRepository.hasAcceptedDisclaimer()
        .collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    when (hasAcceptedDisclaimer) {
        null -> {
            // Loading state while checking preferences
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        false -> {
            // Show disclaimer screen
            DisclaimerScreen(
                onAccept = {
                    scope.launch {
                        userPreferencesRepository.acceptDisclaimer()
                    }
                }
            )
        }
        true -> {
            // Show main app content
            MainAppContent()
        }
    }
}

@Composable
private fun MainAppContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = BottomNavItem.entries.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                LottoBottomNavigationBar(
                    currentDestination = currentDestination,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        LottoNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
