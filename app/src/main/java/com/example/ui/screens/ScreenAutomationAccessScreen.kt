package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Screen Automation Access detail screen directly matching the uploaded screenshots.
 * Explains accessibility capabilities, privacy safeguards, screen reading boundaries,
 * and allows opening Android system accessibility settings.
 */
@Composable
fun ScreenAutomationAccessScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var isCurrentlyOn by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090412)) // Dark navy-violet background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 24.dp)
        ) {
            // Top Bar with rounded square back button
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
                        .testTag("automation_back_button"),
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
                    text = "Screen automation\naccess",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 28.sp
                )
            }

            // Status Pill: Currently on (Exact match to Screenshot)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isCurrentlyOn) Color(0xFF064E3B) else Color(0xFF1E1738),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isCurrentlyOn) Color(0xFF34D399) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCurrentlyOn) "Currently on" else "Disabled",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrentlyOn) Color(0xFF34D399) else Color(0xFF94A3B8)
                    )
                }
            }

            // Intro Explanatory Text (Exact match to Screenshot)
            Text(
                text = "This is optional. RAHUL AI works for talking, opening apps, and calling or messaging contacts without it - you'll just tap Send yourself on messages. Turn it on only if you also want RAHUL AI to act on screen for you, like posting a photo or filling in a form, when you ask it to by voice.",
                fontSize = 14.sp,
                color = Color(0xFFA78BFA).copy(alpha = 0.85f),
                lineHeight = 21.sp,
                modifier = Modifier.padding(bottom = 22.dp)
            )

            // Section 1: ✦ WHAT IT READS (Exact match to Screenshot)
            AutomationSectionHeader(title = "WHAT IT READS")

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 22.dp)
                    .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color(0xFFC084FC),
                        modifier = Modifier
                            .size(22.dp)
                            .padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "The screen of whichever app is open",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Visible text, button labels, and their on-screen positions in the app you're currently using - only while it's carrying out something you asked for. It does not read your screen in the background, and password fields are never captured.",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // Section 2: ✦ WHAT IT CAN DO (Exact match to Screenshot)
            AutomationSectionHeader(title = "WHAT IT CAN DO")

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 22.dp)
                    .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = Color(0xFFC084FC),
                        modifier = Modifier
                            .size(22.dp)
                            .padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Tap, type, and scroll for you",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Only in the app currently in front of you, and only in direct response to a request you made in that conversation - for example \"Instagram par ye photo post karo\". It never acts on its own, and never runs while you're not asking it to do something.",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // Section 3: ✦ WHAT IT NEVER DOES (Exact match to Screenshot 2)
            AutomationSectionHeader(title = "WHAT IT NEVER DOES")

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 26.dp)
                    .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Item 1: No background monitoring
                    RestrictionItem(
                        title = "No background monitoring",
                        description = "It's inactive except while actively carrying out a request you made."
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Item 2: No password capture
                    RestrictionItem(
                        title = "No password capture",
                        description = "Password fields are excluded from every screen reading."
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Item 3: No use outside what you asked
                    RestrictionItem(
                        title = "No use outside what you asked",
                        description = "It doesn't act on other apps, settings, or accounts beyond the task you gave it."
                    )
                }
            }

            // Bottom Button: "Turn off in system settings" (Exact match to Screenshot 2)
            Button(
                onClick = {
                    try {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("automation_toggle_settings_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1C1236),
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF6B46C1).copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = "Turn off in system settings",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AutomationSectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color(0xFFC084FC),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC084FC),
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun RestrictionItem(
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Block,
            contentDescription = null,
            tint = Color(0xFFC084FC),
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 18.sp
            )
        }
    }
}
