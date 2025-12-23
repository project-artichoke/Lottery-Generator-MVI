package com.aaltix.lotto.core.ui.adaptive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centers content with a maximum width constraint for tablets.
 * On phones, content fills the available width.
 * On tablets, content is centered with the specified max width.
 */
@Composable
fun CenteredContent(
    maxWidth: Dp = 600.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

/**
 * Two-column layout for expanded tablets, single column for phones/medium tablets.
 * On expanded tablets (10"+), displays left and right content side by side.
 * On smaller screens, only displays left content.
 */
@Composable
fun AdaptiveTwoColumn(
    modifier: Modifier = Modifier,
    spacing: Dp = 24.dp,
    leftContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit
) {
    if (isExpandedTablet()) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                leftContent()
            }
            Box(modifier = Modifier.weight(1f)) {
                rightContent()
            }
        }
    } else {
        // Single column for phones and medium tablets
        Box(modifier = modifier) {
            leftContent()
        }
    }
}

/**
 * Returns appropriate horizontal padding based on device size.
 * - Phone: 16dp
 * - Medium tablet (7"): 32dp
 * - Expanded tablet (10"+): 48dp
 */
@Composable
fun adaptiveHorizontalPadding(): Dp {
    return when {
        isExpandedTablet() -> 48.dp
        isMediumTablet() -> 32.dp
        else -> 16.dp
    }
}

/**
 * Returns appropriate ball size for main number displays based on device size.
 * - Phone: 56dp
 * - Medium tablet (7"): 64dp
 * - Expanded tablet (10"+): 72dp
 */
@Composable
fun adaptiveBallSize(): Dp {
    return when {
        isExpandedTablet() -> 72.dp
        isMediumTablet() -> 64.dp
        else -> 56.dp
    }
}

/**
 * Returns appropriate small ball size for list items based on device size.
 * - Phone: 36dp
 * - Medium tablet (7"): 42dp
 * - Expanded tablet (10"+): 48dp
 */
@Composable
fun adaptiveSmallBallSize(): Dp {
    return when {
        isExpandedTablet() -> 48.dp
        isMediumTablet() -> 42.dp
        else -> 36.dp
    }
}

/**
 * Returns appropriate grid column count for history list based on device size.
 * - Phone: 1 column (list)
 * - Medium tablet (7"): 2 columns
 * - Expanded tablet (10"+): 3 columns
 */
@Composable
fun adaptiveGridColumns(): Int {
    return when {
        isExpandedTablet() -> 3
        isMediumTablet() -> 2
        else -> 1
    }
}

/**
 * Returns appropriate content max width based on content type.
 * - Form content: 600-700dp
 * - Settings content: 900dp
 * - List content: 800dp
 */
@Composable
fun adaptiveMaxWidth(contentType: AdaptiveContentType = AdaptiveContentType.DEFAULT): Dp {
    return when (contentType) {
        AdaptiveContentType.DEFAULT -> 600.dp
        AdaptiveContentType.FORM -> 700.dp
        AdaptiveContentType.SETTINGS -> 900.dp
        AdaptiveContentType.LIST -> 800.dp
    }
}

/**
 * Content type enum for determining max width constraints.
 */
enum class AdaptiveContentType {
    DEFAULT,
    FORM,
    SETTINGS,
    LIST
}
