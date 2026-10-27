package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.actions.ActionResult
import com.example.actions.PhoneActionManager
import com.example.ai.GeminiAiService
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ConversationEntity
import com.example.data.model.AppThemeMode
import com.example.data.model.AssistantLanguage
import com.example.data.model.PersonalityMode
import com.example.data.model.PhoneAction
import com.example.data.model.QuickActionCategory
import com.example.data.model.UserSettings
import com.example.data.repository.ChatRepository
import com.example.data.repository.PreferencesRepository
import com.example.voice.SpeechInputState
import com.example.voice.VoiceManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "RahulViewModel"

class MyraViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val chatRepo = ChatRepository(db.chatDao(), GeminiAiService())
    private val prefRepo = PreferencesRepository(application)
    val voiceManager = VoiceManager(application)
    val phoneActionManager = PhoneActionManager(application)

    val conversations: StateFlow<List<ConversationEntity>> = chatRepo.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<com.example.data.local.MemoryEntity>> = db.chatDao().getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<com.example.data.local.ReminderEntity>> = db.chatDao().getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<UserSettings> = prefRepo.settings

    private val _currentConversationId = MutableStateFlow<String?>(null)
    val currentConversationId: StateFlow<String?> = _currentConversationId.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val messages: StateFlow<List<ChatMessageEntity>> = _messages.asStateFlow()

    private val _streamingText = MutableStateFlow<String?>(null)
    val streamingText: StateFlow<String?> = _streamingText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isWebSearchEnabled = MutableStateFlow(false)
    val isWebSearchEnabled: StateFlow<Boolean> = _isWebSearchEnabled.asStateFlow()

    private val _selectedCategory = MutableStateFlow<QuickActionCategory?>(null)
    val selectedCategory: StateFlow<QuickActionCategory?> = _selectedCategory.asStateFlow()

    private val _speakingMessageId = MutableStateFlow<String?>(null)
    val speakingMessageId: StateFlow<String?> = _speakingMessageId.asStateFlow()

    private val _errorBanner = MutableStateFlow<String?>(null)
    val errorBanner: StateFlow<String?> = _errorBanner.asStateFlow()

    private var messagesObserverJob: Job? = null
    private var generationJob: Job? = null

    init {
        // Observe voice manager speaking state to clear active message id when speech ends
        viewModelScope.launch {
            voiceManager.isSpeaking.collect { isSpeaking ->
                if (!isSpeaking) {
                    _speakingMessageId.value = null
                }
            }
        }

        // Initialize active conversation
        viewModelScope.launch {
            conversations.collect { list ->
                if (_currentConversationId.value == null && list.isNotEmpty()) {
                    selectConversation(list.first().id)
                }
            }
        }
    }

    fun selectConversation(conversationId: String) {
        _currentConversationId.value = conversationId
        _streamingText.value = null
        _selectedCategory.value = null

        messagesObserverJob?.cancel()
        messagesObserverJob = viewModelScope.launch {
            chatRepo.getMessagesForConversation(conversationId).collect { msgList ->
                _messages.value = msgList
            }
        }
    }

    fun startNewConversation() {
        generationJob?.cancel()
        _isLoading.value = false
        _streamingText.value = null
        _selectedCategory.value = null

        viewModelScope.launch {
            val newConv = chatRepo.createNewConversation()
            selectConversation(newConv.id)
        }
    }

    fun renameConversation(id: String, newTitle: String) {
        viewModelScope.launch {
            chatRepo.renameConversation(id, newTitle)
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            chatRepo.deleteConversation(id)
            if (_currentConversationId.value == id) {
                _currentConversationId.value = null
                _messages.value = emptyList()
            }
        }
    }

    fun clearAllConversations() {
        viewModelScope.launch {
            chatRepo.clearAllConversations()
            _currentConversationId.value = null
            _messages.value = emptyList()
        }
    }

    fun toggleWebSearch() {
        _isWebSearchEnabled.value = !_isWebSearchEnabled.value
    }

    fun selectCategory(category: QuickActionCategory?) {
        _selectedCategory.value = if (_selectedCategory.value?.id == category?.id) null else category
    }

    fun sendMessage(promptText: String) {
        if (promptText.isBlank() || _isLoading.value) return

        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            var activeId = _currentConversationId.value
            if (activeId == null) {
                val newConv = chatRepo.createNewConversation()
                activeId = newConv.id
                selectConversation(activeId)
            }

            val isSearch = _isWebSearchEnabled.value
            val userMsg = chatRepo.insertUserMessage(activeId, promptText, isSearch)

            _isLoading.value = true
            _streamingText.value = ""
            val accumulated = StringBuilder()

            try {
                val recentHistory = chatRepo.getRecentMessages(activeId)

                chatRepo.streamAiResponse(recentHistory, promptText, isSearch, settings.value.personalityMode).collect { chunk ->
                    accumulated.append(chunk)
                    _streamingText.value = accumulated.toString()
                }

                val finalAiResponse = accumulated.toString().trim()
                if (finalAiResponse.isNotEmpty()) {
                    val savedMsg = chatRepo.saveAssistantMessage(activeId, finalAiResponse, isSearch)
                    _streamingText.value = null

                    // Auto Voice TTS if enabled in user settings
                    if (settings.value.isAutoVoiceEnabled) {
                        speakMessage(savedMsg.id, finalAiResponse)
                    }
                } else {
                    chatRepo.saveAssistantMessage(
                        activeId,
                        "Received an empty response from the AI model. Please try asking again.",
                        isSearch,
                        isError = true
                    )
                    _streamingText.value = null
                }
            } catch (e: CancellationException) {
                _streamingText.value = null
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Generation error", e)
                val errorMsg = e.localizedMessage ?: "Could not complete the request. Please check connectivity and retry."
                chatRepo.saveAssistantMessage(activeId, errorMsg, isSearch, isError = true)
                _streamingText.value = null
                _errorBanner.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryLastMessage() {
        val lastUserMessage = _messages.value.lastOrNull { it.role == "user" }
        if (lastUserMessage != null) {
            sendMessage(lastUserMessage.content)
        }
    }

    fun startVoiceInput() {
        val langCode = when (settings.value.preferredLanguage) {
            AssistantLanguage.BENGALI -> "bn-BD"
            AssistantLanguage.HINDI -> "hi-IN"
            else -> "en-US"
        }
        voiceManager.startListening(langCode)
    }

    fun stopVoiceInput() {
        voiceManager.stopListening()
    }

    fun resetSpeechState() {
        voiceManager.resetSpeechState()
    }

    fun speakMessage(messageId: String, text: String) {
        _speakingMessageId.value = messageId
        val langCode = when (settings.value.preferredLanguage) {
            AssistantLanguage.BENGALI -> "bn"
            AssistantLanguage.HINDI -> "hi"
            else -> "en"
        }
        voiceManager.speak(
            text = text,
            languageCode = langCode,
            pitch = settings.value.speechPitch,
            speechRate = settings.value.speechRate
        )
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
        _speakingMessageId.value = null
    }

    fun executePhoneAction(action: PhoneAction) {
        val result = phoneActionManager.executeAction(action)
        when (result) {
            is ActionResult.Success -> {
                _errorBanner.value = result.message
            }
            is ActionResult.Error -> {
                _errorBanner.value = result.message
            }
        }
    }

    fun detectPhoneAction(text: String): PhoneAction? {
        return phoneActionManager.detectPhoneAction(text)
    }

    fun updateTheme(mode: AppThemeMode) {
        prefRepo.setThemeMode(mode)
    }

    fun updateLanguage(language: AssistantLanguage) {
        prefRepo.setLanguage(language)
    }

    fun updatePersonalityMode(personality: PersonalityMode) {
        prefRepo.setPersonalityMode(personality)
    }

    fun toggleAutoVoice(enabled: Boolean) {
        prefRepo.setAutoVoice(enabled)
    }

    fun updateSpeechRate(rate: Float) {
        prefRepo.setSpeechRate(rate)
    }

    fun updateSpeechPitch(pitch: Float) {
        prefRepo.setSpeechPitch(pitch)
    }

    fun updateHeartParticles(enabled: Boolean) {
        prefRepo.setHeartParticlesEnabled(enabled)
    }

    fun clearErrorBanner() {
        _errorBanner.value = null
    }

    // Memory Management
    fun addMemory(title: String, content: String) {
        viewModelScope.launch {
            db.chatDao().insertMemory(
                com.example.data.local.MemoryEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    content = content,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            db.chatDao().deleteMemory(id)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            db.chatDao().deleteAllMemories()
        }
    }

    // Reminders & Timers Management
    fun addReminder(title: String, timeLabel: String, description: String = "") {
        viewModelScope.launch {
            db.chatDao().insertReminder(
                com.example.data.local.ReminderEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    timeLabel = timeLabel,
                    description = description,
                    timestamp = System.currentTimeMillis(),
                    isCompleted = false
                )
            )
        }
    }

    fun toggleReminder(id: String, completed: Boolean) {
        viewModelScope.launch {
            db.chatDao().updateReminderStatus(id, completed)
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            db.chatDao().deleteReminder(id)
        }
    }

    fun launchNativeTimer(seconds: Int) {
        executePhoneAction(
            PhoneAction(
                id = java.util.UUID.randomUUID().toString(),
                type = com.example.data.model.ActionType.SET_TIMER,
                title = "Timer: ${seconds / 60}m",
                description = "Native Android countdown timer",
                timerSeconds = seconds
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}
