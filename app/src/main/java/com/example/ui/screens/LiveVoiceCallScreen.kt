package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.FloatingHeartsField
import com.example.ui.viewmodel.MyraViewModel
import com.example.voice.SpeechInputState

/**
 * Fullscreen Live Voice Call Screen directly matching reference Screenshot 1.
 * Features the radiant celestial breathing orb, live voice interaction, and call controls (Mute, End, Minimize).
 */
@Composable
fun LiveVoiceCallScreen(
    viewModel: MyraViewModel,
    onMinimize: () -> Unit,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val speechInputState by viewModel.voiceManager.speechInputState.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val streamingText by viewModel.streamingText.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var isMuted by remember { mutableStateOf(false) }
    var lastRecognizedText by remember { mutableStateOf("") }

    // Intercept hardware back button to minimize
    BackHandler { onMinimize() }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceInput()
        } else {
            Toast.makeText(context, "Microphone permission is required for voice calling.", Toast.LENGTH_LONG).show()
        }
    }

    // Automatically ensure mic is active on entering call if permission granted
    LaunchedEffect(Unit) {
        val hasMicPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasMicPermission) {
            if (speechInputState !is SpeechInputState.Listening) {
                viewModel.startVoiceInput()
            }
        } else {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Capture recognized speech and automatically trigger AI response
    LaunchedEffect(speechInputState) {
        when (val state = speechInputState) {
            is SpeechInputState.Success -> {
                if (state.recognizedText.isNotBlank()) {
                    lastRecognizedText = state.recognizedText
                    viewModel.sendMessage(state.recognizedText)
                    viewModel.resetSpeechState()
                }
            }
            is SpeechInputState.Error -> {
                viewModel.resetSpeechState()
            }
            else -> {}
        }
    }

    // Breathing pulse animation for the celestial orb
    val infiniteTransition = rememberInfiniteTransition(label = "celestial_orb")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraGlow"
    )

    val statusText = when {
        isSpeaking -> "RAHUL AI is speaking..."
        isLoading || streamingText != null -> "RAHUL AI is thinking..."
        speechInputState is SpeechInputState.Listening -> "RAHUL AI is listening..."
        isMuted -> "RAHUL AI is muted"
        else -> "RAHUL AI is ready"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0618), // Deep cosmic purple
                        Color(0xFF160927),
                        Color(0xFF1B0B30),
                        Color(0xFF090312)
                    )
                )
            )
    ) {
        // Starry particles & subtle ambient floating hearts
        StarrySkyCanvas(modifier = Modifier.fillMaxSize())

        if (settings.isHeartParticlesEnabled) {
            FloatingHeartsField(
                modifier = Modifier.fillMaxSize(),
                particleCount = 12,
                alphaMultiplier = 0.35f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header: "RAHUL AI" + Minimize Button (Matching Screenshot 1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RAHUL AI",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                // Minimize chevron button (top right)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22163A))
                        .border(1.dp, Color(0xFF6B46C1).copy(alpha = 0.3f), CircleShape)
                        .clickable { onMinimize() }
                        .testTag("voice_minimize_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize Call",
                        tint = Color(0xFFE2E8F0),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Center Radiant Celestial Orb
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .scale(breathingScale),
                contentAlignment = Alignment.Center
            ) {
                // Outer glowing aura rings
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFFFF8A65).copy(alpha = 0.35f * auraGlow),
                                    Color(0xFFE91E63).copy(alpha = 0.18f * auraGlow),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Main Radiant 3D Celestial Shaded Sun/Sphere (Exact match to Screenshot 1)
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFFFFF),       // Intense white specular highlight
                                    Color(0xFFFFD180),       // Warm solar yellow
                                    Color(0xFFFFAB91),       // Soft peach/apricot
                                    Color(0xFFF48FB1),       // Warm blush rose
                                    Color(0xFFE57373),       // Coral body
                                    Color(0xFFD81B60)        // Deep shaded magenta perimeter
                                ),
                                center = Offset(140f, 130f),
                                radius = 280f
                            )
                        )
                        .shadow(24.dp, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dynamic Call Status Text (Exact match to Screenshot 1)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Keeps listening if you leave this screen.",
                    fontSize = 13.sp,
                    color = Color(0xFFA78BFA).copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )

                if (lastRecognizedText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "\"$lastRecognizedText\"",
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFFFBBF24),
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom 3 Action Controls: Mute | End | Minimize (Exact match to Screenshot 1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Mute Button
                CallActionButton(
                    icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = if (isMuted) "Unmute" else "Mute",
                    buttonColor = if (isMuted) Color(0xFF6B21A8) else Color(0xFF261A44),
                    iconColor = Color.White,
                    size = 62.dp,
                    onClick = {
                        isMuted = !isMuted
                        if (isMuted) {
                            viewModel.stopVoiceInput()
                        } else {
                            viewModel.startVoiceInput()
                        }
                    },
                    testTag = "voice_call_mute_button"
                )

                // 2. End Call Button (Large pulsating coral-red button)
                CallActionButton(
                    icon = Icons.Default.CallEnd,
                    label = "End",
                    buttonColor = Color(0xFFFB7185), // Soft coral red matching screenshot
                    iconColor = Color.White,
                    size = 76.dp,
                    onClick = {
                        viewModel.stopVoiceInput()
                        viewModel.stopSpeaking()
                        onEndCall()
                    },
                    testTag = "voice_call_end_button"
                )

                // 3. Minimize Button
                CallActionButton(
                    icon = Icons.Default.KeyboardArrowDown,
                    label = "Minimize",
                    buttonColor = Color(0xFF261A44),
                    iconColor = Color.White,
                    size = 62.dp,
                    onClick = onMinimize,
                    testTag = "voice_call_minimize_dock_button"
                )
            }
        }
    }
}

@Composable
private fun CallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    buttonColor: Color,
    iconColor: Color,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(buttonColor)
                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                .clickable { onClick() }
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(size * 0.42f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFFCBD5E1),
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Draws subtle twinkling galaxy star dust matching Screenshot 1 background.
 */
@Composable
private fun StarrySkyCanvas(modifier: Modifier = Modifier) {
    val stars = remember {
        val rand = java.util.Random(777L)
        List(40) {
            Triple(rand.nextFloat(), rand.nextFloat(), 1.5f + rand.nextFloat() * 2.5f)
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        stars.forEach { (xFrac, yFrac, radius) ->
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = radius,
                center = Offset(xFrac * w, yFrac * h)
            )
        }
    }
}
