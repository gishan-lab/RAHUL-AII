package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BottomNavDock
import com.example.ui.components.NavTab

/**
 * Permissions Screen directly matching reference Screenshot 7.
 * Shows allowed device permissions, privacy controls, and links to system settings.
 */
@Composable
fun PermissionsScreen(
    onNavigateToAutomation: () -> Unit = {},
    onBack: () -> Unit,
    onTabSelected: (NavTab) -> Unit,
    onOpenVoiceCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                    .padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1B1130))
                        .clickable { onBack() }
                        .testTag("permissions_back_button"),
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
                    text = "Permissions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = "Allow only what you want RAHUL AI to use. You can change any of these later.",
                fontSize = 14.sp,
                color = Color(0xFFA78BFA).copy(alpha = 0.8f),
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Permissions Card List (Exact match to Screenshot 7)
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    // 1. Microphone
                    PermissionItemRow(
                        icon = Icons.Default.Mic,
                        title = "Microphone",
                        subtitle = "Needed for voice conversations",
                        badgeText = "Allowed",
                        badgeIsActive = true,
                        onClick = {}
                    )

                    // 2. Contacts
                    PermissionItemRow(
                        icon = Icons.Default.Contacts,
                        title = "Contacts",
                        subtitle = "Lets RAHUL AI find a number when you say a name",
                        badgeText = "Allowed",
                        badgeIsActive = true,
                        onClick = {}
                    )

                    // 3. Phone Calls
                    PermissionItemRow(
                        icon = Icons.Default.Call,
                        title = "Phone Calls",
                        subtitle = "RAHUL AI can place calls directly",
                        badgeText = "Allowed",
                        badgeIsActive = true,
                        onClick = {}
                    )

                    // 4. Accessibility
                    PermissionItemRow(
                        icon = Icons.Default.Accessibility,
                        title = "Accessibility",
                        subtitle = "On - RAHUL AI can tap, type and scroll on screen",
                        badgeText = "Details",
                        badgeIsActive = false,
                        onClick = onNavigateToAutomation
                    )

                    // 5. WhatsApp Messages
                    PermissionItemRow(
                        icon = Icons.Default.Message,
                        title = "WhatsApp Messages",
                        subtitle = "On - RAHUL AI can check unread WhatsApp messages",
                        badgeText = "Allowed",
                        badgeIsActive = true,
                        onClick = {}
                    )

                    // 6. Listening Glow Outside RAHUL AI
                    PermissionItemRow(
                        icon = Icons.Default.Layers,
                        title = "Listening Glow Outside RAHUL AI",
                        subtitle = "On - the active-session border shows even when RAHUL AI is in the background",
                        badgeText = "Allowed",
                        badgeIsActive = true,
                        onClick = {}
                    )

                    // 7. Notifications
                    PermissionItemRow(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Reminders and alerts from RAHUL AI",
                        badgeText = "Open Settings",
                        badgeIsActive = false,
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
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
private fun PermissionItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeText: String,
    badgeIsActive: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                tint = Color(0xFFC084FC),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Badge Pill (Matching Screenshot 7)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (badgeIsActive) Color(0xFF064E3B) else Color(0xFF1E1738)
        ) {
            Text(
                text = badgeText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (badgeIsActive) Color(0xFF34D399) else Color(0xFFC084FC),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF6B46C1),
            modifier = Modifier.size(18.dp)
        )
    }
}
