package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccentPink
import com.example.ui.theme.VioletSecondary
import kotlin.math.PI
import kotlin.math.sin

/**
 * Romantic heart particle data representation
 */
private data class HeartParticle(
    val id: Int,
    val startXFrac: Float,
    val startYFrac: Float,
    val sizePx: Float,
    val speed: Float,
    val swayAmplitude: Float,
    val swayCycles: Float,
    val baseAlpha: Float,
    val baseRotation: Float,
    val color: Color
)

/**
 * Fullscreen or container-level floating heart particle effect.
 * Creates an elegant, ambient romantic aura with soft drifting hearts.
 */
@Composable
fun FloatingHeartsField(
    modifier: Modifier = Modifier,
    particleCount: Int = 18,
    minSize: Dp = 10.dp,
    maxSize: Dp = 24.dp,
    alphaMultiplier: Float = 1.0f,
    enabled: Boolean = true
) {
    if (!enabled) return

    val romanticPalette = remember {
        listOf(
            AccentPink,                         // #FF2A6D
            Color(0xFFF472B6),                  // Soft rose pink
            VioletSecondary,                    // #8A2BE2 Romantic violet
            Color(0xFFE11D48),                  // Crimson romantic red
            Color(0xFFD946EF),                  // Vibrant magenta
            Color(0xFFFB7185)                   // Blush pink
        )
    }

    // Pre-generate stable pseudo-random properties for each particle
    val particles = remember(particleCount) {
        val rand = java.util.Random(42L)
        List(particleCount) { idx ->
            HeartParticle(
                id = idx,
                startXFrac = rand.nextFloat(),
                startYFrac = rand.nextFloat(),
                sizePx = 28f + rand.nextFloat() * 42f, // roughly 10dp to 25dp
                speed = 0.7f + rand.nextFloat() * 0.7f,
                swayAmplitude = 18f + rand.nextFloat() * 30f,
                swayCycles = 1.0f + rand.nextFloat() * 1.5f,
                baseAlpha = 0.14f + rand.nextFloat() * 0.22f,
                baseRotation = -18f + rand.nextFloat() * 36f,
                color = romanticPalette[rand.nextInt(romanticPalette.size)]
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "floating_hearts")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heart_progress"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        particles.forEach { p ->
            // Calculate current cycle progress (0f..1f)
            val currentProg = (progress * p.speed + p.startYFrac) % 1.0f

            // Drifts gracefully upward from bottom to top
            val currentY = h * (1.0f - currentProg)

            // Horizontal sine sway
            val swayAngle = (currentProg * 2.0 * PI * p.swayCycles).toFloat()
            val currentX = (p.startXFrac * w) + sin(swayAngle) * p.swayAmplitude

            // Gentle fade-in at bottom and fade-out near top
            val fadeIn = if (currentProg < 0.12f) (currentProg / 0.12f) else 1.0f
            val fadeOut = if (currentProg > 0.82f) ((1.0f - currentProg) / 0.18f).coerceAtLeast(0f) else 1.0f
            val alpha = (p.baseAlpha * fadeIn * fadeOut * alphaMultiplier).coerceIn(0f, 1f)

            if (alpha > 0.01f) {
                val rotation = p.baseRotation + sin(swayAngle) * 14f
                val heartPath = createHeartPath(currentX, currentY, p.sizePx)

                rotate(degrees = rotation, pivot = Offset(currentX, currentY)) {
                    // Soft translucent heart fill
                    drawPath(
                        path = heartPath,
                        color = p.color.copy(alpha = alpha)
                    )
                }
            }
        }
    }
}

/**
 * Concentrated romantic heart aura drifting closely around the AI Voice Orb.
 */
@Composable
fun OrbHeartAura(
    modifier: Modifier = Modifier,
    particleCount: Int = 10,
    enabled: Boolean = true
) {
    if (!enabled) return

    val particles = remember(particleCount) {
        val rand = java.util.Random(1337L)
        List(particleCount) { idx ->
            HeartParticle(
                id = idx,
                startXFrac = 0.2f + rand.nextFloat() * 0.6f, // Centered around orb
                startYFrac = rand.nextFloat(),
                sizePx = 22f + rand.nextFloat() * 26f,
                speed = 0.8f + rand.nextFloat() * 0.6f,
                swayAmplitude = 12f + rand.nextFloat() * 22f,
                swayCycles = 1.0f + rand.nextFloat(),
                baseAlpha = 0.22f + rand.nextFloat() * 0.28f,
                baseRotation = -15f + rand.nextFloat() * 30f,
                color = if (idx % 2 == 0) AccentPink else Color(0xFFF472B6)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "orb_hearts")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_heart_prog"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        particles.forEach { p ->
            val currentProg = (progress * p.speed + p.startYFrac) % 1.0f
            val currentY = h * (1.0f - currentProg)
            val swayAngle = (currentProg * 2.0 * PI * p.swayCycles).toFloat()
            val currentX = (p.startXFrac * w) + sin(swayAngle) * p.swayAmplitude

            val fadeIn = if (currentProg < 0.15f) (currentProg / 0.15f) else 1.0f
            val fadeOut = if (currentProg > 0.80f) ((1.0f - currentProg) / 0.20f).coerceAtLeast(0f) else 1.0f
            val alpha = (p.baseAlpha * fadeIn * fadeOut).coerceIn(0f, 1f)

            if (alpha > 0.01f) {
                val rotation = p.baseRotation + sin(swayAngle) * 10f
                val heartPath = createHeartPath(currentX, currentY, p.sizePx)

                rotate(degrees = rotation, pivot = Offset(currentX, currentY)) {
                    drawPath(
                        path = heartPath,
                        color = p.color.copy(alpha = alpha)
                    )
                }
            }
        }
    }
}

/**
 * Generates an organic, smooth cubic bezier heart path centered at (cx, cy).
 */
private fun createHeartPath(cx: Float, cy: Float, size: Float): Path {
    val path = Path()
    val half = size / 2f
    val top = cy - half
    val left = cx - half
    val right = cx + half
    val bottom = top + size

    // Bottom tip of the heart
    path.moveTo(cx, top + size * 0.88f)

    // Left lower lobe curve up to side
    path.cubicTo(
        left + size * 0.05f, top + size * 0.58f,
        left, top + size * 0.32f,
        left, top + size * 0.18f
    )

    // Left crest arch over the top into center cleft
    path.cubicTo(
        left, top,
        cx - size * 0.08f, top,
        cx, top + size * 0.22f
    )

    // Right crest arch from center cleft over the top
    path.cubicTo(
        cx + size * 0.08f, top,
        right, top,
        right, top + size * 0.18f
    )

    // Right lower lobe curve back down to bottom tip
    path.cubicTo(
        right, top + size * 0.32f,
        right - size * 0.05f, top + size * 0.58f,
        cx, top + size * 0.88f
    )

    path.close()
    return path
}
