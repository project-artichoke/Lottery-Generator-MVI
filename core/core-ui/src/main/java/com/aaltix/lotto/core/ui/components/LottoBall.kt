package com.aaltix.lotto.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaltix.lotto.core.ui.theme.LottoBallBlue
import com.aaltix.lotto.core.ui.theme.LottoBallGreen
import com.aaltix.lotto.core.ui.theme.LottoBallOrange
import com.aaltix.lotto.core.ui.theme.LottoBallPurple
import com.aaltix.lotto.core.ui.theme.LottoBallRed
import com.aaltix.lotto.core.ui.theme.LottoBonusRed
import com.aaltix.lotto.core.ui.theme.LottoTheme

/**
 * A lottery ball composable with bounce animation.
 *
 * @param number The number to display on the ball
 * @param isBonus Whether this is a bonus ball (displayed with different color)
 * @param index The index used for staggered animation delay
 * @param animate Whether to animate the ball appearance
 * @param size The size of the ball
 * @param modifier Modifier for the composable
 */
@Composable
fun LottoBall(
    number: Int,
    isBonus: Boolean = false,
    index: Int = 0,
    animate: Boolean = true,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(if (animate) 0f else 1f) }
    val offsetY = remember { Animatable(if (animate) -100f else 0f) }

    LaunchedEffect(number, animate) {
        if (animate) {
            delay(index * 100L)
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
        }
    }

    val ballColor = if (isBonus) {
        LottoBonusRed
    } else {
        getBallColor(index)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                translationY = offsetY.value
            }
            .size(size)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ballColor.copy(alpha = 1f),
                        ballColor.copy(alpha = 0.8f),
                        ballColor.copy(alpha = 0.9f)
                    )
                )
            )
    ) {
        // Inner highlight for 3D effect
        Box(
            modifier = Modifier
                .size(size * 0.85f)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.value * 0.5f
                    )
                )
        )

        Text(
            text = number.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = (size.value * 0.4f).sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
    }
}

private fun getBallColor(index: Int): Color {
    return when (index % 5) {
        0 -> LottoBallBlue
        1 -> LottoBallGreen
        2 -> LottoBallOrange
        3 -> LottoBallPurple
        else -> LottoBallRed
    }
}

@Preview(showBackground = true)
@Composable
private fun LottoBallPreview() {
    LottoTheme {
        LottoBall(number = 42, index = 0, animate = false)
    }
}

@Preview(showBackground = true)
@Composable
private fun LottoBonusBallPreview() {
    LottoTheme {
        LottoBall(number = 7, isBonus = true, animate = false)
    }
}
