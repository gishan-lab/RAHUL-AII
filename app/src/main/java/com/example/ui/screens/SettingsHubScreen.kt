package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserSettings
import com.example.ui.components.BottomNavDock
import com.example.ui.components.NavTab
import com.example.ui.theme.AccentPink
import com.example.ui.theme.VioletSecondary

/**
 * Settings Hub Screen directly matching reference Screenshot 4.
 * Features the RAHUL AI header card, Community and Video tutorial banners, and AI Settings list.
 */
@Composable
fun SettingsHubScreen(
    settings: UserSettings,
    totalConversationsCount: Int,
    onNavigateToConnections: () -> Unit,
    onNavigateToVoiceLang: () -> Unit,
    onNavigateToPhoneControl: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToMemory: () -> Unit,
    onNavigateToReminders: () -> Unit,
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
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1B1130))
                        .clickable { onBack() }
                        .testTag("settings_hub_back_button"),
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

                Column {
                    Text(
                        text = "RAHUL AI",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA78BFA)
                    )
                }
            }

            Text(
                text = "Customize your AI experience",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Top Profile Card (Matching Screenshot 4)
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF281845))
                            .border(1.5.dp, AccentPink, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.rahul_ai_logo),
                            contentDescription = "RAHUL AI Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(52.dp).clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "RAHUL AI",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Your Personal AI Assistant",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "v1.5 • Made with ❤️",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF6B46C1),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Banner 1: Join community (Matching Screenshot 4)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF190D32)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .border(1.dp, Color(0xFF8A2BE2).copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me"))
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = Color(0xFFC084FC),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Join community",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE9D5FF),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFC084FC),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Banner 2: Watch the setup video (Matching Screenshot 4)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF190D32)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com"))
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Watch the setup video",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "API keys, permissions and Accessibility, step by step",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF6B46C1),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Section: ✦ AI SETTINGS (Matching Screenshot 4)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFC084FC),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI SETTINGS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Configure your AI assistant",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Grouped AI Settings Card (Matching Screenshot 4)
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    // 1. Connections
                    SettingsHubItem(
                        icon = Icons.Default.Key,
                        iconTint = Color(0xFFC084FC),
                        title = "Connections",
                        subtitle = "Gemini and Groq keys",
                        badgeText = "Ready",
                        onClick = onNavigateToConnections
                    )

                    // 2. Voice & language
                    SettingsHubItem(
                        icon = Icons.Default.RecordVoiceOver,
                        iconTint = Color(0xFF818CF8),
                        title = "Voice & language",
                        subtitle = "Achernar • ${settings.preferredLanguage.displayName} • ${String.format("%.2f", settings.speechRate)}x",
                        onClick = onNavigateToVoiceLang
                    )

                    // 3. Chats & memory
                    SettingsHubItem(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        iconTint = Color(0xFFC084FC),
                        title = "Chats & memory",
                        subtitle = "$totalConversationsCount saved chats",
                        onClick = onNavigateToMemory
                    )

                    // 4. Phone control
                    SettingsHubItem(
                        icon = Icons.Default.PhoneAndroid,
                        iconTint = Color(0xFFA78BFA),
                        title = "Phone control",
                        subtitle = "Quick actions & accessibility",
                        onClick = onNavigateToPhoneControl
                    )

                    // 5. Permissions
                    SettingsHubItem(
                        icon = Icons.Default.Lock,
                        iconTint = Color(0xFF60A5FA),
                        title = "Permissions",
                        subtitle = "Manage device permissions",
                        onClick = onNavigateToPermissions
                    )

                    // 6. Reminders & Timers
                    SettingsHubItem(
                        icon = Icons.Default.Alarm,
                        iconTint = AccentPink,
                        title = "Reminders & Timers",
                        subtitle = "Alarms, countdowns & notifications",
                        onClick = onNavigateToReminders
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
private fun SettingsHubItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        if (badgeText != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF064E3B),
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF6B46C1),
            modifier = Modifier.size(20.dp)
        )
    }
}
