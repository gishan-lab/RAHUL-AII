package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class NavTab {
    HOME,
    CHAT,
    HISTORY,
    SETTINGS
}

/**
 * Floating Bottom Navigation Dock matching the reference screenshots.
 * Features 4 tabs (Home, Chat, History, Settings) and a center elevated 3D glowing Voice Orb.
 */
@Composable
fun BottomNavDock(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onOrbClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_dock_pulse")
    val orbPulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbPulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Dock Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(16.dp, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xF0120B24), // Dark translucent violet
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF8A2BE2).copy(alpha = 0.45f),
                        Color(0xFFFF2A6D).copy(alpha = 0.35f),
                        Color(0xFF8A2BE2).copy(alpha = 0.45f)
                    )
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tab 1: Home
                NavDockItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = currentTab == NavTab.HOME,
                    onClick = { onTabSelected(NavTab.HOME) },
                    testTag = "nav_tab_home",
                    modifier = Modifier.weight(1f)
                )

                // Tab 2: Chat
                NavDockItem(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    label = "Chat",
                    isSelected = currentTab == NavTab.CHAT,
                    onClick = { onTabSelected(NavTab.CHAT) },
                    testTag = "nav_tab_chat",
                    modifier = Modifier.weight(1f)
                )

                // Space for Center Elevated 3D Orb
                Spacer(modifier = Modifier.width(68.dp))

                // Tab 4: History
                NavDockItem(
                    icon = Icons.Default.History,
                    label = "History",
                    isSelected = currentTab == NavTab.HISTORY,
                    onClick = { onTabSelected(NavTab.HISTORY) },
                    testTag = "nav_tab_history",
                    modifier = Modifier.weight(1f)
                )

                // Tab 5: Settings
                NavDockItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isSelected = currentTab == NavTab.SETTINGS,
                    onClick = { onTabSelected(NavTab.SETTINGS) },
                    testTag = "nav_tab_settings",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Center Elevated Glowing 3D Orb Button (Exact match to reference screenshots)
        Box(
            modifier = Modifier
                .offset(y = (-14).dp)
                .size(66.dp)
                .scale(orbPulse)
                .clickable { onOrbClick() }
                .testTag("nav_center_orb"),
            contentAlignment = Alignment.Center
        ) {
            // Outer golden/bronze halo ring
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFFF59E0B).copy(alpha = 0.5f),
                                Color(0xFFD97706).copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFFBBF24),
                                Color(0xFFB45309)
                            )
                        ),
                        CircleShape
                    )
            )

            // Inner 3D Shaded Purple Sphere
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),       // Specular white highlight
                                Color(0xFFE9D5FF),       // Soft lavender sheen
                                Color(0xFFA855F7),       // Radiant violet
                                Color(0xFF7E22CE),       // Deep purple body
                                Color(0xFF3B0764)        // Dark shaded edge
                            ),
                            center = androidx.compose.ui.geometry.Offset(42f, 38f),
                            radius = 65f
                        )
                    )
                    .shadow(8.dp, CircleShape)
            )
        }
    }
}

@Composable
private fun NavDockItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val activeColor = Color(0xFFFBBF24) // Gold / warm yellow active tint
    val inactiveColor = Color(0xFFA78BFA).copy(alpha = 0.65f)

    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else inactiveColor
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Active indicator underline bar
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(16.dp)
                .background(
                    if (isSelected) activeColor else Color.Transparent,
                    RoundedCornerShape(1.dp)
                )
        )
    }
}
