package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentPink
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.VioletSecondary

enum class OrbState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

@Composable
fun CentralAiOrb(
    state: OrbState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "orb_anim")

    val pulseScale by transition.animateFloat(
        initialValue = when (state) {
            OrbState.LISTENING -> 0.95f
            OrbState.THINKING -> 1.0f
            OrbState.SPEAKING -> 0.92f
            OrbState.IDLE -> 0.96f
        },
        targetValue = when (state) {
            OrbState.LISTENING -> 1.25f
            OrbState.THINKING -> 1.08f
            OrbState.SPEAKING -> 1.22f
            OrbState.IDLE -> 1.04f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    OrbState.LISTENING -> 500
                    OrbState.THINKING -> 800
                    OrbState.SPEAKING -> 450
                    OrbState.IDLE -> 1400
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val rotationAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == OrbState.THINKING) 1800 else 6000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )

    val primaryGlowColor = when (state) {
        OrbState.LISTENING -> AccentPink
        OrbState.THINKING -> VioletSecondary
        OrbState.SPEAKING -> CyanPrimary
        OrbState.IDLE -> CyanPrimary
    }

    val secondaryGlowColor = when (state) {
        OrbState.LISTENING -> VioletSecondary
        OrbState.THINKING -> CyanPrimary
        OrbState.SPEAKING -> VioletSecondary
        OrbState.IDLE -> VioletSecondary
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .clickable { onClick() }
        ) {
            // Outer dynamic aura canvas
            Canvas(
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale)
                    .rotate(rotationAngle)
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryGlowColor.copy(alpha = 0.25f),
                            secondaryGlowColor.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 2f
                )

                // Cybernetic orbit rings
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(primaryGlowColor, secondaryGlowColor, Color.Transparent, primaryGlowColor)
                    ),
                    radius = (size.minDimension / 2f) * 0.95f,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Core Orb Body
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryGlowColor,
                                secondaryGlowColor,
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.linearGradient(listOf(primaryGlowColor, secondaryGlowColor)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (state) {
                        OrbState.LISTENING -> Icons.Default.Mic
                        OrbState.THINKING -> Icons.Default.Psychology
                        OrbState.SPEAKING -> Icons.Default.GraphicEq
                        OrbState.IDLE -> Icons.Default.AutoAwesome
                    },
                    contentDescription = "RAHUL AI Orb",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = when (state) {
                OrbState.LISTENING -> "Listening to your voice..."
                OrbState.THINKING -> "Thinking & Reasoning..."
                OrbState.SPEAKING -> "Speaking..."
                OrbState.IDLE -> "Tap orb to speak"
            },
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryGlowColor
            )
        )
    }
}
