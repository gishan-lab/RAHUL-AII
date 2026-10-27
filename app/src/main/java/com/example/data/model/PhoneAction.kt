package com.example.data.model

enum class ActionType {
    OPEN_YOUTUBE,
    OPEN_CAMERA,
    OPEN_MAPS,
    OPEN_DIALER,
    SEND_SMS,
    OPEN_SETTINGS,
    SET_TIMER,
    OPEN_URL
}

data class PhoneAction(
    val id: String,
    val type: ActionType,
    val title: String,
    val description: String,
    val intentUri: String? = null,
    val packageName: String? = null,
    val extraText: String? = null,
    val timerSeconds: Int? = null,
    val phoneNumber: String? = null
)

enum class PersonalityMode(val id: String, val displayName: String, val description: String) {
    SMART("smart", "Smart Assistant", "Intelligent, polite, helpful & articulate companion"),
    GF_LOVE("gf_love", "GF / Romantic Mode", "Caring, sweet, affectionate, emotionally warm & playful"),
    STUDY_TUTOR("study", "Study Tutor", "Patient academic mentor with clear step-by-step guidance"),
    CODE_EXPERT("coder", "Code & Tech Expert", "Software engineer explaining algorithms, syntax & architectures")
}

data class DynamicPrompt(
    val text: String,
    val category: String,
    val iconName: String = "auto_awesome"
)
