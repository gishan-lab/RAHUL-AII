package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssistantLanguage
import com.example.data.model.PersonalityMode
import com.example.data.model.UserSettings
import com.example.ui.components.BottomNavDock
import com.example.ui.components.NavTab
import com.example.ui.theme.AccentPink
import com.example.ui.theme.CyanPrimary

/**
 * Voice & Language Settings Screen directly matching reference Screenshot 3.
 * Configures assistant identity, personality (GF mode), language, voice models, speech speed, and startup listening.
 */
@Composable
fun VoiceLanguageScreen(
    settings: UserSettings,
    onUpdateLanguage: (AssistantLanguage) -> Unit,
    onUpdatePersonality: (PersonalityMode) -> Unit,
    onUpdateSpeechRate: (Float) -> Unit,
    onToggleAutoVoice: (Boolean) -> Unit,
    onBack: () -> Unit,
    onTabSelected: (NavTab) -> Unit,
    onOpenVoiceCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showPersonalityDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }

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
                .padding(bottom = 80.dp) // clearance for dock
        ) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1B1130))
                        .clickable { onBack() }
                        .testTag("voice_lang_back_button"),
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
                    text = "Voice & language",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Card 1: Identity & Language Options (Matching Screenshot 3)
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    // Assistant name
                    SettingActionRow(
                        icon = Icons.Default.AutoAwesome,
                        iconTint = Color(0xFFC084FC),
                        title = "Assistant name",
                        subtitle = "RAHUL AI",
                        onClick = {}
                    )

                    // Personality
                    SettingActionRow(
                        icon = Icons.Default.Face,
                        iconTint = AccentPink,
                        title = "Personality",
                        subtitle = if (settings.personalityMode == PersonalityMode.GF_LOVE) "GF Mode" else settings.personalityMode.displayName,
                        onClick = { showPersonalityDialog = true }
                    )

                    // Language
                    SettingActionRow(
                        icon = Icons.Default.Language,
                        iconTint = Color(0xFF60A5FA),
                        title = "Language",
                        subtitle = settings.preferredLanguage.displayName,
                        onClick = { showLanguageDialog = true }
                    )

                    // Response style
                    SettingActionRow(
                        icon = Icons.Default.Tune,
                        iconTint = Color(0xFFA78BFA),
                        title = "Response style",
                        subtitle = "Detailed",
                        onClick = {}
                    )
                }
            }

            // Section 2: HOW HE/SHE SOUNDS (Matching Screenshot 3)
            SectionHeader(title = "HOW HE/SHE SOUNDS")

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    // Voice
                    SettingActionRow(
                        icon = Icons.Default.RecordVoiceOver,
                        iconTint = Color(0xFFC084FC),
                        title = "Voice",
                        subtitle = "Achernar - Soft",
                        onClick = {}
                    )

                    // Voice model
                    SettingActionRow(
                        icon = Icons.Default.AutoAwesome,
                        iconTint = Color(0xFFC084FC),
                        title = "Voice model",
                        subtitle = "Gemini 2.5 Flash Live",
                        onClick = {}
                    )

                    // Speech speed
                    SettingActionRow(
                        icon = Icons.Default.Speed,
                        iconTint = Color(0xFF38BDF8),
                        title = "Speech speed",
                        subtitle = "${String.format("%.2f", settings.speechRate)}x",
                        onClick = { showSpeedDialog = true }
                    )

                    // Voice volume
                    SettingActionRow(
                        icon = Icons.Default.VolumeUp,
                        iconTint = Color(0xFFF472B6),
                        title = "Voice volume",
                        subtitle = "90%",
                        onClick = {}
                    )
                }
            }

            // Section 3: LISTENING (Matching Screenshot 3)
            SectionHeader(title = "LISTENING")

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF281845)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = Color(0xFFC084FC),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Listen when the app opens",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Starts listening as you open",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Switch(
                        checked = settings.isAutoVoiceEnabled,
                        onCheckedChange = { onToggleAutoVoice(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF9333EA)
                        )
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

    // Personality Selection Dialog
    if (showPersonalityDialog) {
        AlertDialog(
            onDismissRequest = { showPersonalityDialog = false },
            title = { Text("Select Personality", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    PersonalityMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdatePersonality(mode)
                                    showPersonalityDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (mode == PersonalityMode.GF_LOVE) "GF Mode (Romantic)" else mode.displayName,
                                fontSize = 15.sp,
                                fontWeight = if (settings.personalityMode == mode) FontWeight.Bold else FontWeight.Normal,
                                color = if (settings.personalityMode == mode) AccentPink else Color.White
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPersonalityDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    AssistantLanguage.entries.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.displayName,
                                fontSize = 15.sp,
                                fontWeight = if (settings.preferredLanguage == lang) FontWeight.Bold else FontWeight.Normal,
                                color = if (settings.preferredLanguage == lang) Color(0xFF60A5FA) else Color.White
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Speech Speed Dialog
    if (showSpeedDialog) {
        var currentSpeed by remember { mutableStateOf(settings.speechRate) }
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Speech Speed: ${String.format("%.2f", currentSpeed)}x") },
            text = {
                Slider(
                    value = currentSpeed,
                    onValueChange = { currentSpeed = it },
                    valueRange = 0.5f..1.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFC084FC),
                        activeTrackColor = Color(0xFF9333EA)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateSpeechRate(currentSpeed)
                    showSpeedDialog = false
                }) {
                    Text("Apply")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
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
private fun SettingActionRow(
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF281845)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
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
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Details",
            tint = Color(0xFF6B46C1),
            modifier = Modifier.size(20.dp)
        )
    }
}
