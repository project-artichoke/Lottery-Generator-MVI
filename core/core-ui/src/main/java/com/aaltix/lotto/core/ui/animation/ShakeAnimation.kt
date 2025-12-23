package com.aaltix.lotto.core.ui.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Creates a shake animation modifier.
 *
 * @param enabled Whether the shake animation is enabled
 * @param intensity The shake intensity in pixels
 */
@Composable
fun Modifier.shakeAnimation(
    enabled: Boolean,
    intensity: Float = 10f
): Modifier {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(enabled) {
        if (enabled) {
            shakeOffset.animateTo(
                targetValue = intensity,
                animationSpec = infiniteRepeatable(
                    animation = tween(50, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            shakeOffset.animateTo(0f)
        }
    }

    return this.graphicsLayer {
        translationX = if (enabled) shakeOffset.value else 0f
    }
}

/**
 * State holder for shake animation.
 */
class ShakeState {
    val offsetX = Animatable(0f)

    suspend fun shake(
        iterations: Int = 4,
        intensity: Float = 10f,
        durationMillis: Int = 50
    ) {
        repeat(iterations) {
            offsetX.animateTo(intensity, tween(durationMillis))
            offsetX.animateTo(-intensity, tween(durationMillis))
        }
        offsetX.animateTo(0f, tween(durationMillis))
    }
}

@Composable
fun rememberShakeState(): ShakeState {
    return remember { ShakeState() }
}

@Composable
fun Modifier.shake(state: ShakeState): Modifier {
    return this.graphicsLayer {
        translationX = state.offsetX.value
    }
}
