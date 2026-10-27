package com.example.data.model

enum class ChatRole {
    USER,
    ASSISTANT,
    ERROR
}

data class Citation(
    val title: String,
    val url: String
)

data class QuickActionCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val prompts: List<SuggestedPrompt>
)

data class SuggestedPrompt(
    val title: String,
    val prompt: String,
    val category: String,
    val iconName: String = "auto_awesome"
)

enum class AppThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

enum class AssistantLanguage(val code: String, val displayName: String, val greeting: String) {
    AUTO("auto", "Auto Detect", "Hello! I'm RAHUL, your smart personal AI assistant. How can I help you today?"),
    ENGLISH("en", "English", "Hello! I'm RAHUL, your smart personal AI assistant. How can I help you today?"),
    BENGALI("bn", "বাংলা (Bengali)", "নমস্কার! আমি রাহুল (RAHUL), আপনার স্মার্ট পার্সোনাল এআই অ্যাসিস্ট্যান্ট। আজ আপনাকে কীভাবে সাহায্য করতে পারি?"),
    HINDI("hi", "हिंदी (Hindi)", "नमस्ते! मैं राहुल (RAHUL) हूँ, आपकी स्मार्ट पर्सनल एआई असिस्टेंट। आज मैं आपकी क्या सहायता कर सकता हूँ?")
}

data class UserSettings(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val preferredLanguage: AssistantLanguage = AssistantLanguage.AUTO,
    val personalityMode: PersonalityMode = PersonalityMode.SMART,
    val isAutoVoiceEnabled: Boolean = false,
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val isWebSearchGroundingDefault: Boolean = false,
    val isHeartParticlesEnabled: Boolean = true
)
