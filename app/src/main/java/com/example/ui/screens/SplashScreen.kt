package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AccentPink
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.VioletSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinishSplash: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "hearts")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(700))
        scale.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        // Show for ~2.8 seconds then transition to home
        delay(2600)
        onFinishSplash()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF09030E),
                        Color(0xFF19061F),
                        Color(0xFF28072E),
                        Color(0xFF0C0312)
                    )
                )
            )
            .clickable { onFinishSplash() }, // Allow instant tap to skip
        contentAlignment = Alignment.Center
    ) {
        // Ambient romantic floating heart particle field
        com.example.ui.components.FloatingHeartsField(
            modifier = Modifier.fillMaxSize(),
            particleCount = 22,
            alphaMultiplier = 1.3f
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // RAHUL AI Romantic Heart-Robot Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(170.dp)
                    .scale(glowScale)
            ) {
                // Pulsing outer aura ring
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(AccentPink.copy(alpha = 0.35f), Color.Transparent)
                            )
                        )
                )

                Image(
                    painter = painterResource(id = R.drawable.rahul_ai_logo),
                    contentDescription = "RAHUL AI Romantic Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(145.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .border(
                            2.5.dp,
                            Brush.linearGradient(listOf(AccentPink, VioletSecondary, CyanPrimary)),
                            RoundedCornerShape(32.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Branding Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "RAHUL",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 32.sp,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = AccentPink,
                        fontSize = 32.sp,
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your Personal AI Assistant ❤️",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF472B6),
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // App Owner / Creator Section with authentic portrait
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color(0x331E0629),
                modifier = Modifier
                    .border(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(
                                AccentPink.copy(alpha = 0.4f),
                                VioletSecondary.copy(alpha = 0.4f)
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.owner_portrait),
                        contentDescription = "Rahul - App Creator",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, AccentPink, CircleShape)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rahul",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = AccentPink,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = "Creator & Personal AI Architect",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Quick Enter Button
            ElevatedButton(
                onClick = onFinishSplash,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = AccentPink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}


