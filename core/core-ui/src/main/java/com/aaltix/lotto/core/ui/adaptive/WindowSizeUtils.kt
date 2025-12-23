package com.aaltix.lotto.core.ui.adaptive

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal for providing WindowSizeClass throughout the app.
 * Must be provided at the app level (MainActivity).
 */
val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("No WindowSizeClass provided. Make sure to provide it in MainActivity.")
}

/**
 * Returns true if the device is considered a tablet (medium or expanded width).
 * Medium: 600dp - 840dp (7" tablets)
 * Expanded: 840dp+ (10" tablets)
 */
@Composable
fun isTablet(): Boolean {
    val windowSizeClass = LocalWindowSizeClass.current
    return windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
}

/**
 * Returns true if the device is an expanded tablet (10"+, 840dp+ width).
 */
@Composable
fun isExpandedTablet(): Boolean {
    val windowSizeClass = LocalWindowSizeClass.current
    return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
}

/**
 * Returns true if the device is a medium tablet (7"-10", 600dp-840dp width).
 */
@Composable
fun isMediumTablet(): Boolean {
    val windowSizeClass = LocalWindowSizeClass.current
    return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium
}

/**
 * Returns true if the device is a phone (compact width, < 600dp).
 */
@Composable
fun isCompact(): Boolean {
    val windowSizeClass = LocalWindowSizeClass.current
    return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
}
