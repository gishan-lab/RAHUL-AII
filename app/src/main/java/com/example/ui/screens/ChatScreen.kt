package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.DynamicSuggestionEngine
import com.example.data.local.ChatMessageEntity
import com.example.ui.components.BottomNavDock
import com.example.ui.components.ChatBubble
import com.example.ui.components.ChatInputBar
import com.example.ui.components.DynamicSuggestionsBar
import com.example.ui.components.FloatingHeartsField
import com.example.ui.components.NavTab
import com.example.ui.components.VoiceVisualizer
import com.example.ui.theme.AccentPink
import com.example.ui.theme.CyanPrimary
import com.example.ui.viewmodel.MyraViewModel
import com.example.voice.SpeechInputState

/**
 * Dedicated Chat Screen for full interactive AI conversations with RAHUL AI.
 * Shows conversation messages, live streaming responses, phone actions, dynamic prompts, and input bar.
 */
@Composable
fun ChatScreen(
    viewModel: MyraViewModel,
    onBack: () -> Unit,
    onTabSelected: (NavTab) -> Unit,
    onOpenVoiceCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val listState = rememberLazyListState()
    val messages by viewModel.messages.collectAsState()
    val streamingText by viewModel.streamingText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentConvId by viewModel.currentConversationId.collectAsState()
    val isWebSearchEnabled by viewModel.isWebSearchEnabled.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val speakingMessageId by viewModel.speakingMessageId.collectAsState()
    val speechInputState by viewModel.voiceManager.speechInputState.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var inputMessage by remember { mutableStateOf("") }

    // Intercept speech recognition
    LaunchedEffect(speechInputState) {
        when (val state = speechInputState) {
            is SpeechInputState.Success -> {
                inputMessage = if (inputMessage.isBlank()) state.recognizedText
                else "$inputMessage ${state.recognizedText}"
                viewModel.resetSpeechState()
            }
            else -> {}
        }
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, streamingText) {
        val totalCount = messages.size + if (streamingText != null) 1 else 0
        if (totalCount > 0) {
            listState.animateScrollToItem(totalCount - 1)
        }
    }

    // Dynamic prompt suggestions
    val dynamicSuggestions = remember(messages, selectedCategory, settings.personalityMode, settings.preferredLanguage) {
        com.example.ai.DynamicSuggestionEngine.getDynamicSuggestions(
            selectedCategory = selectedCategory,
            recentMessages = messages,
            personalityMode = settings.personalityMode,
            language = settings.preferredLanguage
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090412))
    ) {
        if (settings.isHeartParticlesEnabled) {
            FloatingHeartsField(
                modifier = Modifier.fillMaxSize(),
                particleCount = 10,
                alphaMultiplier = 0.35f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Chat Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B1130))
                        .clickable { onBack() }
                        .testTag("chat_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RAHUL AI Chat",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Online • English, বাংলা, हिंदी",
                        fontSize = 11.sp,
                        color = Color(0xFF34D399)
                    )
                }

                // New Chat Button
                IconButton(
                    onClick = { viewModel.startNewConversation() },
                    modifier = Modifier.testTag("chat_new_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = Color(0xFFC084FC)
                    )
                }
            }

            // Messages List or Empty State
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty() && streamingText == null) {
                    // Empty Chat Helper State
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Start a conversation with RAHUL AI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ask questions in English, Bengali, or Hindi, or speak naturally using voice.",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            val detectedAction = if (msg.role == "assistant") {
                                viewModel.detectPhoneAction(msg.content)
                            } else null

                            ChatBubble(
                                message = msg,
                                isSpeakingThis = speakingMessageId == msg.id,
                                onSpeak = { text -> viewModel.speakMessage(msg.id, text) },
                                onStopSpeaking = { viewModel.stopSpeaking() },
                                onRetry = { viewModel.retryLastMessage() },
                                detectedAction = detectedAction,
                                onExecuteAction = { action -> viewModel.executePhoneAction(action) }
                            )
                        }

                        if (streamingText != null) {
                            item {
                                val dummyStreamMessage = ChatMessageEntity(
                                    id = "streaming_live",
                                    conversationId = currentConvId ?: "",
                                    role = "assistant",
                                    content = streamingText ?: "",
                                    timestamp = System.currentTimeMillis(),
                                    isWebSearchUsed = isWebSearchEnabled
                                )
                                ChatBubble(
                                    message = dummyStreamMessage,
                                    isSpeakingThis = false,
                                    onSpeak = {},
                                    onStopSpeaking = {},
                                    onRetry = {},
                                    detectedAction = null,
                                    onExecuteAction = null
                                )
                            }
                        }
                    }
                }
            }

            // Suggestions bar & Input Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                DynamicSuggestionsBar(
                    suggestions = dynamicSuggestions,
                    onSuggestionClick = { clicked ->
                        inputMessage = clicked
                        viewModel.sendMessage(clicked)
                    }
                )

                VoiceVisualizer(
                    isListening = speechInputState is SpeechInputState.Listening,
                    onStopListening = { viewModel.stopVoiceInput() }
                )

                ChatInputBar(
                    inputText = inputMessage,
                    onInputChange = { inputMessage = it },
                    onSendMessage = { text ->
                        viewModel.sendMessage(text)
                        inputMessage = ""
                    },
                    isLoading = isLoading,
                    isListening = speechInputState is SpeechInputState.Listening,
                    onStartListening = { viewModel.startVoiceInput() },
                    onStopListening = { viewModel.stopVoiceInput() },
                    isWebSearchEnabled = isWebSearchEnabled,
                    onToggleWebSearch = { viewModel.toggleWebSearch() }
                )
            }
        }
    }
}
