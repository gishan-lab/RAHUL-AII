package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppThemeMode
import com.example.data.model.AssistantLanguage
import com.example.data.model.PersonalityMode
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("rahul_ai_user_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        val themeStr = prefs.getString(KEY_THEME, AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name
        val theme = try { AppThemeMode.valueOf(themeStr) } catch (_: Exception) { AppThemeMode.DARK }

        val langCode = prefs.getString(KEY_LANGUAGE, AssistantLanguage.AUTO.code) ?: AssistantLanguage.AUTO.code
        val lang = AssistantLanguage.entries.find { it.code == langCode } ?: AssistantLanguage.AUTO

        val personalityStr = prefs.getString(KEY_PERSONALITY, PersonalityMode.SMART.name) ?: PersonalityMode.SMART.name
        val personality = try { PersonalityMode.valueOf(personalityStr) } catch (_: Exception) { PersonalityMode.SMART }

        val autoVoice = prefs.getBoolean(KEY_AUTO_VOICE, false)
        val rate = prefs.getFloat(KEY_SPEECH_RATE, 1.0f)
        val pitch = prefs.getFloat(KEY_SPEECH_PITCH, 1.0f)
        val webSearch = prefs.getBoolean(KEY_WEB_SEARCH, false)
        val heartParticles = prefs.getBoolean(KEY_HEART_PARTICLES, true)

        return UserSettings(
            themeMode = theme,
            preferredLanguage = lang,
            personalityMode = personality,
            isAutoVoiceEnabled = autoVoice,
            speechRate = rate,
            speechPitch = pitch,
            isWebSearchGroundingDefault = webSearch,
            isHeartParticlesEnabled = heartParticles
        )
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun setLanguage(language: AssistantLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
        _settings.value = _settings.value.copy(preferredLanguage = language)
    }

    fun setPersonalityMode(personality: PersonalityMode) {
        prefs.edit().putString(KEY_PERSONALITY, personality.name).apply()
        _settings.value = _settings.value.copy(personalityMode = personality)
    }

    fun setAutoVoice(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_VOICE, enabled).apply()
        _settings.value = _settings.value.copy(isAutoVoiceEnabled = enabled)
    }

    fun setSpeechRate(rate: Float) {
        prefs.edit().putFloat(KEY_SPEECH_RATE, rate).apply()
        _settings.value = _settings.value.copy(speechRate = rate)
    }

    fun setSpeechPitch(pitch: Float) {
        prefs.edit().putFloat(KEY_SPEECH_PITCH, pitch).apply()
        _settings.value = _settings.value.copy(speechPitch = pitch)
    }

    fun setWebSearchDefault(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WEB_SEARCH, enabled).apply()
        _settings.value = _settings.value.copy(isWebSearchGroundingDefault = enabled)
    }

    fun setHeartParticlesEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HEART_PARTICLES, enabled).apply()
        _settings.value = _settings.value.copy(isHeartParticlesEnabled = enabled)
    }

    companion object {
        private const val KEY_THEME = "key_theme_mode"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_PERSONALITY = "key_personality_mode"
        private const val KEY_AUTO_VOICE = "key_auto_voice"
        private const val KEY_SPEECH_RATE = "key_speech_rate"
        private const val KEY_SPEECH_PITCH = "key_speech_pitch"
        private const val KEY_WEB_SEARCH = "key_web_search"
        private const val KEY_HEART_PARTICLES = "key_heart_particles"
    }
}
