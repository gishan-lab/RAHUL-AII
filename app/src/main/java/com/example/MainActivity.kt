package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppThemeMode
import com.example.ui.components.NavTab
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ConnectionsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveVoiceCallScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.PermissionsScreen
import com.example.ui.screens.PhoneControlScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.ScreenAutomationAccessScreen
import com.example.ui.screens.SettingsHubScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VoiceLanguageScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MyraViewModel
import com.example.util.configureAppCheck

private enum class Screen {
    SPLASH,
    HOME,
    LIVE_VOICE_CALL,
    CHAT,
    SETTINGS_HUB,
    SETTINGS_CONNECTIONS,
    SETTINGS_VOICE_LANG,
    SETTINGS_PHONE_CONTROL,
    SETTINGS_PERMISSIONS,
    SCREEN_AUTOMATION,
    MEMORY,
    REMINDERS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Firebase App Check with launch intent
        configureAppCheck(this, intent)

        // Enable edge-to-edge system insets
        enableEdgeToEdge()

        setContent {
            val myraViewModel: MyraViewModel = viewModel()
            val userSettings by myraViewModel.settings.collectAsState()
            val conversations by myraViewModel.conversations.collectAsState()
            val memories by myraViewModel.memories.collectAsState()
            val reminders by myraViewModel.reminders.collectAsState()

            val isDarkTheme = when (userSettings.themeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }

                    // Hardware back navigation handler
                    BackHandler(enabled = currentScreen != Screen.HOME && currentScreen != Screen.SPLASH) {
                        when (currentScreen) {
                            Screen.SCREEN_AUTOMATION -> currentScreen = Screen.SETTINGS_PERMISSIONS
                            Screen.SETTINGS_CONNECTIONS,
                            Screen.SETTINGS_VOICE_LANG,
                            Screen.SETTINGS_PHONE_CONTROL,
                            Screen.SETTINGS_PERMISSIONS -> currentScreen = Screen.SETTINGS_HUB
                            Screen.MEMORY, Screen.REMINDERS -> currentScreen = Screen.SETTINGS_HUB
                            Screen.LIVE_VOICE_CALL, Screen.CHAT, Screen.SETTINGS_HUB -> currentScreen = Screen.HOME
                            else -> currentScreen = Screen.HOME
                        }
                    }

                    fun navigateToTab(tab: NavTab) {
                        currentScreen = when (tab) {
                            NavTab.HOME -> Screen.HOME
                            NavTab.CHAT -> Screen.CHAT
                            NavTab.HISTORY -> Screen.MEMORY
                            NavTab.SETTINGS -> Screen.SETTINGS_HUB
                        }
                    }

                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            Screen.SPLASH -> {
                                SplashScreen(
                                    onFinishSplash = { currentScreen = Screen.HOME }
                                )
                            }
                            Screen.HOME -> {
                                HomeScreen(
                                    viewModel = myraViewModel,
                                    onNavigateToSettings = { currentScreen = Screen.SETTINGS_HUB },
                                    onNavigateToChat = { currentScreen = Screen.CHAT },
                                    onNavigateToHistory = { currentScreen = Screen.MEMORY },
                                    onOpenVoiceCall = { currentScreen = Screen.LIVE_VOICE_CALL },
                                    onSelectQuickPrompt = { prompt ->
                                        myraViewModel.sendMessage(prompt)
                                        currentScreen = Screen.CHAT
                                    }
                                )
                            }
                            Screen.LIVE_VOICE_CALL -> {
                                LiveVoiceCallScreen(
                                    viewModel = myraViewModel,
                                    onMinimize = { currentScreen = Screen.HOME },
                                    onEndCall = { currentScreen = Screen.HOME }
                                )
                            }
                            Screen.CHAT -> {
                                ChatScreen(
                                    viewModel = myraViewModel,
                                    onBack = { currentScreen = Screen.HOME },
                                    onTabSelected = ::navigateToTab,
                                    onOpenVoiceCall = { currentScreen = Screen.LIVE_VOICE_CALL }
                                )
                            }
                            Screen.SETTINGS_HUB -> {
                                SettingsHubScreen(
                                    settings = userSettings,
                                    totalConversationsCount = conversations.size,
                                    onNavigateToConnections = { currentScreen = Screen.SETTINGS_CONNECTIONS },
                                    onNavigateToVoiceLang = { currentScreen = Screen.SETTINGS_VOICE_LANG },
                                    onNavigateToPhoneControl = { currentScreen = Screen.SETTINGS_PHONE_CONTROL },
                                    onNavigateToPermissions = { currentScreen = Screen.SETTINGS_PERMISSIONS },
                                    onNavigateToMemory = { currentScreen = Screen.MEMORY },
                                    onNavigateToReminders = { currentScreen = Screen.REMINDERS },
                                    onBack = { currentScreen = Screen.HOME },
                                    onTabSelected = ::navigateToTab,
                                    onOpenVoiceCall = { currentScreen = Screen.LIVE_VOICE_CALL }
                                )
                            }
                            Screen.SETTINGS_CONNECTIONS -> {
                                ConnectionsScreen(
                                    onBack = { currentScreen = Screen.SETTINGS_HUB },
                                    onTabSelected = ::navigateToTab,
                                    onOpenVoiceCall = { currentScreen = Screen.LIVE_VOICE_CALL }
                                )
                            }
                            Screen.SETTINGS_VOICE_LANG -> {
                                VoiceLanguageScreen(
                                    settings = userSettings,
                                    onUpdateLanguage = { myraViewModel.updateLanguage(it) },
                                    onUpdatePersonality = { myraViewModel.updatePersonalityMode(it) },
                                    onUpdateSpeechRate = { myraViewModel.updateSpeechRate(it) },
                                    onToggleAutoVoice = { myraViewModel.toggleAutoVoice(it) },
                                    onBack = { currentScreen = Screen.SETTINGS_HUB },
                                    onTabSelected = ::navigateToTab,
                                    onOpenVoiceCall = { currentScreen = Screen.LIVE_VOICE_CALL }
                                )
                            }
                            Screen.SETTINGS_PHONE_CONTROL -> {
                                PhoneControlScreen(
                                    onNavigateToPermissions = { currentScreen = Screen.SETTINGS_PERMISSIONS },
                                    onNavigateToAutomation = { currentScreen = Screen.SCREEN_AUTOMATION },
                                    onBack = { currentScreen = Screen.SETTINGS_HUB },
                                    onTabSelected = ::navigateToTab,
                                    onOpenVoiceCall = { currentScreen = Screen.LIVE_VOICE_CALL }
                                )
                            }
                            Screen.SETTINGS_PERMISSIONS -> {
                                PermissionsScreen(
                                    onNavigateToAutomation = { currentScreen = Screen.SCREEN_AUTOMATION },
                                    onBack = { currentScreen = Screen.SETTINGS_HUB },
                                    onTabSelected = ::navigateToTab,
                                    onOpenVoiceCall = { currentScreen = Screen.LIVE_VOICE_CALL }
                                )
                            }
                            Screen.SCREEN_AUTOMATION -> {
                                ScreenAutomationAccessScreen(
                                    onBack = { currentScreen = Screen.SETTINGS_PERMISSIONS }
                                )
                            }
                            Screen.MEMORY -> {
                                MemoryScreen(
                                    memories = memories,
                                    onAddMemory = { title, content -> myraViewModel.addMemory(title, content) },
                                    onDeleteMemory = { id -> myraViewModel.deleteMemory(id) },
                                    onClearAllMemories = { myraViewModel.clearAllMemories() },
                                    onBack = { currentScreen = Screen.HOME }
                                )
                            }
                            Screen.REMINDERS -> {
                                RemindersScreen(
                                    reminders = reminders,
                                    onAddReminder = { title, timeLabel, desc -> myraViewModel.addReminder(title, timeLabel, desc) },
                                    onToggleReminder = { id, completed -> myraViewModel.toggleReminder(id, completed) },
                                    onDeleteReminder = { id -> myraViewModel.deleteReminder(id) },
                                    onLaunchNativeTimer = { seconds -> myraViewModel.launchNativeTimer(seconds) },
                                    onBack = { currentScreen = Screen.HOME }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
