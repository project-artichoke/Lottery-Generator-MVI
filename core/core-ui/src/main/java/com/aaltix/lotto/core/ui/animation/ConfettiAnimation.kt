package com.aaltix.lotto.core.ui.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private object ConfettiDefaults {
    // Particle spawn area
    const val SPAWN_X_MIN = 0.2f
    const val SPAWN_X_RANGE = 0.6f
    const val SPAWN_Y_RANGE = 0.15f

    // Particle movement
    const val ANGLE_MIN = 60f
    const val ANGLE_RANGE = 60f
    const val SPEED_MIN = 0.5f
    const val SPEED_RANGE = 1.5f
    const val VELOCITY_X_MULTIPLIER = 0.02f
    const val VELOCITY_Y_MULTIPLIER = 0.015f

    // Particle appearance
    const val SIZE_MIN = 6f
    const val SIZE_RANGE = 10f
    const val ROTATION_RANGE = 360f

    // Physics
    const val GRAVITY = 0.0004f
    const val TIME_DIVISOR = 50f
    const val MAX_Y_POSITION = 1.2f

    // Fade timing
    const val FADE_START_THRESHOLD = 0.7f
    const val FADE_DURATION_FRACTION = 0.3f

    // Animation
    const val END_DELAY_MILLIS = 100L
}

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotation: Float
)

/**
 * Confetti animation overlay.
 *
 * @param isActive Whether the confetti animation should play
 * @param particleCount Number of confetti particles
 * @param durationMillis Animation duration
 * @param onAnimationEnd Callback when animation finishes
 */
@Composable
fun ConfettiEffect(
    isActive: Boolean,
    particleCount: Int = 60,
    durationMillis: Int = 3500,
    onAnimationEnd: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }

    val particles = remember(isActive) {
        if (isActive) {
            List(particleCount) {
                val startX = Random.nextFloat() * ConfettiDefaults.SPAWN_X_RANGE + ConfettiDefaults.SPAWN_X_MIN
                val startY = Random.nextFloat() * ConfettiDefaults.SPAWN_Y_RANGE
                val angle = Random.nextFloat() * ConfettiDefaults.ANGLE_RANGE + ConfettiDefaults.ANGLE_MIN
                val speed = Random.nextFloat() * ConfettiDefaults.SPEED_RANGE + ConfettiDefaults.SPEED_MIN
                ConfettiParticle(
                    x = startX,
                    y = startY,
                    color = confettiColors.random(),
                    size = Random.nextFloat() * ConfettiDefaults.SIZE_RANGE + ConfettiDefaults.SIZE_MIN,
                    velocityX = cos(Math.toRadians(angle.toDouble())).toFloat() * speed * ConfettiDefaults.VELOCITY_X_MULTIPLIER,
                    velocityY = sin(Math.toRadians(angle.toDouble())).toFloat() * speed * ConfettiDefaults.VELOCITY_Y_MULTIPLIER,
                    rotation = Random.nextFloat() * ConfettiDefaults.ROTATION_RANGE
                )
            }
        } else {
            emptyList()
        }
    }

    LaunchedEffect(isActive) {
        if (isActive) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
            )
            delay(ConfettiDefaults.END_DELAY_MILLIS)
            onAnimationEnd()
        }
    }

    if (isActive && particles.isNotEmpty()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val currentProgress = progress.value

            particles.forEach { particle ->
                val time = currentProgress * durationMillis / ConfettiDefaults.TIME_DIVISOR
                val x = particle.x + particle.velocityX * time
                val y = particle.y + particle.velocityY * time + ConfettiDefaults.GRAVITY * time * time
                val alpha = if (currentProgress > ConfettiDefaults.FADE_START_THRESHOLD) {
                    1f - ((currentProgress - ConfettiDefaults.FADE_START_THRESHOLD) / ConfettiDefaults.FADE_DURATION_FRACTION)
                } else {
                    1f
                }

                if (alpha > 0f && y < ConfettiDefaults.MAX_Y_POSITION) {
                    drawCircle(
                        color = particle.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                        radius = particle.size,
                        center = Offset(x * size.width, y * size.height)
                    )
                }
            }
        }
    }
}

private val confettiColors = listOf(
    Color(0xFFFFD700), // Gold
    Color(0xFFFF6B6B), // Red
    Color(0xFF4ECDC4), // Teal
    Color(0xFF45B7D1), // Blue
    Color(0xFFFF8C42), // Orange
    Color(0xFF9B59B6), // Purple
    Color(0xFF2ECC71), // Green
    Color(0xFFF39C12)  // Yellow
)
