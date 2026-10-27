package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ui.components.BottomNavDock
import com.example.ui.components.FloatingHeartsField
import com.example.ui.components.HistoryDrawerContent
import com.example.ui.components.NavTab
import com.example.ui.theme.AccentPink
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.MyraViewModel
import com.example.voice.SpeechInputState
import kotlinx.coroutines.launch

/**
 * Main Home Screen directly matching reference Screenshot 2.
 * Features the Top Bar, Hero Companion Avatar card with neon handwriting,
 * Tap to Talk glowing circular mic, "Ask me anything..." search pill,
 * 3 Quick Action Cards (Chat, Create Image, Write), and the floating BottomNavDock with 3D Orb.
 */
@Composable
fun HomeScreen(
    viewModel: MyraViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onOpenVoiceCall: () -> Unit,
    onSelectQuickPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val conversations by viewModel.conversations.collectAsState()
    val currentConvId by viewModel.currentConversationId.collectAsState()
    val speechInputState by viewModel.voiceManager.speechInputState.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onOpenVoiceCall()
        } else {
            Toast.makeText(context, "Microphone permission is required for voice calling.", Toast.LENGTH_LONG).show()
        }
    }

    // Outer Navigation Drawer for Chat History
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF130924),
                drawerContentColor = Color.White
            ) {
                HistoryDrawerContent(
                    conversations = conversations,
                    activeConversationId = currentConvId,
                    onSelectConversation = { id ->
                        viewModel.selectConversation(id)
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToChat()
                    },
                    onNewChat = {
                        viewModel.startNewConversation()
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToChat()
                    },
                    onRenameConversation = { id, title -> viewModel.renameConversation(id, title) },
                    onDeleteConversation = { id -> viewModel.deleteConversation(id) },
                    onClearAll = { viewModel.clearAllConversations() },
                    onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF090412)) // Deep dark violet
        ) {
            // Subtle ambient floating hearts
            if (settings.isHeartParticlesEnabled) {
                FloatingHeartsField(
                    modifier = Modifier.fillMaxSize(),
                    particleCount = 14,
                    alphaMultiplier = 0.4f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .padding(bottom = 86.dp) // clearance for dock
            ) {
                // Top App Bar: Hamburger | "RAHUL AI" | Settings (Matching Screenshot 2)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Rounded square Hamburger Menu button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1B1130))
                            .border(1.dp, Color(0xFF3E2269).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable { coroutineScope.launch { drawerState.open() } }
                            .testTag("home_menu_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Drawer",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Center: "RAHUL AI" + Tagline
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RAHUL",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = Color(0xFFA855F7)
                            )
                        }
                        Text(
                            text = "YOUR AI ASSISTANT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Right: Rounded square Settings button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1B1130))
                            .border(1.dp, Color(0xFF3E2269).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable { onNavigateToSettings() }
                            .testTag("home_settings_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Hero Companion Avatar Card with Neon Handwriting (Matching Screenshot 2)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color(0xFF160D2A))
                        .border(
                            1.5.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFA855F7).copy(alpha = 0.6f),
                                    Color(0xFFFF2A6D).copy(alpha = 0.4f),
                                    Color(0xFF160D2A)
                                )
                            ),
                            RoundedCornerShape(26.dp)
                        )
                ) {
                    // Companion Avatar Image
                    Image(
                        painter = painterResource(id = R.drawable.rahul_ai_avatar),
                        contentDescription = "RAHUL AI Companion",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Ambient Dark Gradient Scrim overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0x33090412),
                                        Color.Transparent,
                                        Color(0xD9090412)
                                    )
                                )
                            )
                    )

                    // Neon Handwriting Text Overlays (Exact match to Screenshot 2)
                    // Top-Left: "Hi, I'm RAHUL AI ♡"
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Hi,\nI'm\nRAHUL AI",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFFF472B6), // Soft neon pink
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFF472B6),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Top-Right: "Always Here For You ♡"
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(18.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Always\nHere\nFor You",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFFC084FC), // Soft neon violet
                            textAlign = TextAlign.End,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFC084FC),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Bottom Creator Badge (Rahul)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xB3150C28),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .border(1.dp, Color(0xFF8A2BE2).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.owner_portrait),
                                contentDescription = "Rahul - Creator",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, AccentPink, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Personalized for Rahul ❤️",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Prominent Voice Interaction: Glowing Circular Mic + "Tap to Talk" (Matching Screenshot 2)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "mic_glow")
                    val micScale by infiniteTransition.animateFloat(
                        initialValue = 0.97f,
                        targetValue = 1.05f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1600, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "micScale"
                    )

                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .scale(micScale)
                            .clickable {
                                val hasAudioPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasAudioPermission) {
                                    onOpenVoiceCall()
                                } else {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                            .testTag("tap_to_talk_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer glowing gradient ring (Exact match to Screenshot 2)
                        Box(
                            modifier = Modifier
                                .size(108.dp)
                                .clip(CircleShape)
                                .border(
                                    3.5.dp,
                                    Brush.sweepGradient(
                                        listOf(
                                            Color(0xFFFF2A6D), // Hot pink
                                            Color(0xFFA855F7), // Violet
                                            Color(0xFF38BDF8), // Cyan blue
                                            Color(0xFFFF2A6D)
                                        )
                                    ),
                                    CircleShape
                                )
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            Color(0xFF7E22CE).copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Inner Dark Disc Button with Mic Icon
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF140B28))
                                .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.5f), CircleShape)
                                .shadow(8.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Tap to Talk",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tap to Talk",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Waveform dots indicator: ··|········|·· (Matching Screenshot 2)
                    Text(
                        text = "··|········|··",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA855F7).copy(alpha = 0.7f),
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Search Input Pill: ✦ Ask me anything... [Send] (Matching Screenshot 2)
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = Color(0xFF140B28),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .border(1.dp, Color(0xFF4C1D95).copy(alpha = 0.45f), RoundedCornerShape(26.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left purple sparkler icon
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF281845)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFC084FC),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Text input field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    text = "Ask me anything...",
                                    fontSize = 14.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_ask_anything_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (searchQuery.isNotBlank()) {
                                        viewModel.sendMessage(searchQuery.trim())
                                        searchQuery = ""
                                        onNavigateToChat()
                                    }
                                }
                            )
                        )

                        // Right: Purple circular send button with paper airplane
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9333EA))
                                .clickable {
                                    if (searchQuery.isNotBlank()) {
                                        viewModel.sendMessage(searchQuery.trim())
                                        searchQuery = ""
                                        onNavigateToChat()
                                    } else {
                                        onNavigateToChat()
                                    }
                                }
                                .testTag("home_send_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Quick Action Cards (3 items matching Screenshot 2):
                // Card 1: Chat | Card 2: Create Image | Card 3: Write
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Chat
                    QuickActionFeatureCard(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        title = "Chat",
                        subtitle = "Get instant answers",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToChat,
                        testTag = "home_card_chat"
                    )

                    // Card 2: Create Image
                    QuickActionFeatureCard(
                        icon = Icons.Default.Image,
                        title = "Create Image",
                        subtitle = "Photos into sketches",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onSelectQuickPrompt("Generate a digital art illustration of romantic neon aesthetics")
                            onNavigateToChat()
                        },
                        testTag = "home_card_create_image"
                    )

                    // Card 3: Write
                    QuickActionFeatureCard(
                        icon = Icons.Default.Description,
                        title = "Write",
                        subtitle = "Notes, code, content",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onSelectQuickPrompt("Write a beautiful romantic message in Bengali and English")
                            onNavigateToChat()
                        },
                        testTag = "home_card_write"
                    )
                }
            }

            // Floating Bottom Dock with Central Elevated 3D Orb (Matching Screenshot 2)
            BottomNavDock(
                currentTab = NavTab.HOME,
                onTabSelected = { tab ->
                    when (tab) {
                        NavTab.HOME -> {}
                        NavTab.CHAT -> onNavigateToChat()
                        NavTab.HISTORY -> onNavigateToHistory()
                        NavTab.SETTINGS -> onNavigateToSettings()
                    }
                },
                onOrbClick = onOpenVoiceCall,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun QuickActionFeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF140B28)),
        modifier = modifier
            .border(1.dp, Color(0xFF4C1D95).copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 14.sp
            )
        }
    }
}
