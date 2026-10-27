package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BottomNavDock
import com.example.ui.components.NavTab

/**
 * Phone Control Screen directly matching reference Screenshot 6.
 * Manages Quick actions, App permissions, floating orb features, and accessibility status.
 */
@Composable
fun PhoneControlScreen(
    onNavigateToPermissions: () -> Unit,
    onNavigateToAutomation: () -> Unit = {},
    onBack: () -> Unit,
    onTabSelected: (NavTab) -> Unit,
    onOpenVoiceCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090412))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 80.dp)
        ) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1B1130))
                        .clickable { onBack() }
                        .testTag("phone_control_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Phone control",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Grouped Actions Card (Matching Screenshot 6)
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    // Quick actions
                    PhoneControlRow(
                        icon = Icons.Default.AutoAwesome,
                        iconTint = Color(0xFFC084FC),
                        title = "Quick actions",
                        subtitle = "Set up the tools RAHUL AI can run",
                        onClick = {}
                    )

                    // App permissions
                    PhoneControlRow(
                        icon = Icons.Default.Lock,
                        iconTint = Color(0xFFA78BFA),
                        title = "App permissions",
                        subtitle = "Microphone, contacts, calls",
                        onClick = onNavigateToPermissions
                    )

                    // Features
                    PhoneControlRow(
                        icon = Icons.Default.Widgets,
                        iconTint = Color(0xFFC084FC),
                        title = "Features",
                        subtitle = "Floating orb, floating avatar, triggers",
                        onClick = {}
                    )

                    // Accessibility Status
                    PhoneControlRow(
                        icon = Icons.Default.Accessibility,
                        iconTint = Color(0xFF818CF8),
                        title = "Accessibility Status",
                        subtitle = "On - tap, type, scroll automation ready",
                        onClick = onNavigateToAutomation
                    )
                }
            }
        }

        // Floating Bottom Dock
        BottomNavDock(
            currentTab = NavTab.SETTINGS,
            onTabSelected = onTabSelected,
            onOrbClick = onOpenVoiceCall,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun PhoneControlRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF281845)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open",
            tint = Color(0xFF6B46C1),
            modifier = Modifier.size(20.dp)
        )
    }
}
