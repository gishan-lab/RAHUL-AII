package com.example.ai

import com.example.data.local.ChatMessageEntity
import com.example.data.model.AssistantLanguage
import com.example.data.model.DynamicPrompt
import com.example.data.model.PersonalityMode
import com.example.data.model.QuickActionCategory

object DynamicSuggestionEngine {

    /**
     * Dynamically computes prompt suggestions based on:
     * 1. Currently selected category (Study, Writing, Translation, General Knowledge, Coding)
     * 2. Recent conversation history and context
     * 3. Active personality mode (Smart, GF/Love, Study Tutor, Code Expert)
     * 4. Preferred language (English, Bengali, Hindi)
     */
    fun getDynamicSuggestions(
        selectedCategory: QuickActionCategory?,
        recentMessages: List<ChatMessageEntity>,
        personalityMode: PersonalityMode,
        language: AssistantLanguage
    ): List<DynamicPrompt> {
        val lastAssistantMessage = recentMessages.lastOrNull { it.role == "assistant" }?.content?.lowercase().orEmpty()
        val lastUserMessage = recentMessages.lastOrNull { it.role == "user" }?.content?.lowercase().orEmpty()
        val combinedLastContext = "$lastUserMessage $lastAssistantMessage"

        // 1. If a Quick Action Category is currently selected, provide high-relevance targeted prompts
        if (selectedCategory != null) {
            return when (selectedCategory.id) {
                "study" -> listOf(
                    DynamicPrompt("Explain this step by step", "study", "school"),
                    DynamicPrompt("Create 3 study flashcards", "study", "auto_awesome"),
                    DynamicPrompt("Quiz me on this concept", "study", "lightbulb"),
                    DynamicPrompt("Summarize in 3 bullet points", "study", "edit_note"),
                    DynamicPrompt("Give a real-world example", "study", "school")
                )
                "writing" -> listOf(
                    DynamicPrompt("Make this more professional", "writing", "edit_note"),
                    DynamicPrompt("Shorten into a polite email", "writing", "edit_note"),
                    DynamicPrompt("Check grammar and tone", "writing", "auto_awesome"),
                    DynamicPrompt("Rewrite with engaging flair", "writing", "edit_note")
                )
                "translation" -> listOf(
                    DynamicPrompt("Translate to Bengali (বাংলা)", "translation", "translate"),
                    DynamicPrompt("Translate to Hindi (हिंदी)", "translation", "translate"),
                    DynamicPrompt("How to pronounce in Banglish?", "translation", "translate"),
                    DynamicPrompt("Is this formal or casual?", "translation", "translate")
                )
                "coding" -> listOf(
                    DynamicPrompt("Show Kotlin code example", "coding", "code"),
                    DynamicPrompt("How to handle error cases?", "coding", "code"),
                    DynamicPrompt("Optimize time complexity", "coding", "code"),
                    DynamicPrompt("Explain how this works line by line", "coding", "code")
                )
                "knowledge" -> listOf(
                    DynamicPrompt("Tell me fascinating facts about this", "knowledge", "lightbulb"),
                    DynamicPrompt("Explain the historical background", "knowledge", "school"),
                    DynamicPrompt("What are the pros and cons?", "knowledge", "lightbulb"),
                    DynamicPrompt("Explain the science simply", "knowledge", "lightbulb")
                )
                else -> emptyList()
            }
        }

        // 2. If there is conversation context, generate intelligent follow-up suggestions
        if (recentMessages.isNotEmpty()) {
            val suggestions = mutableListOf<DynamicPrompt>()

            // Context: Coding / Technical
            if (combinedLastContext.contains("fun ") || combinedLastContext.contains("val ") ||
                combinedLastContext.contains("code") || combinedLastContext.contains("kotlin") ||
                combinedLastContext.contains("android") || combinedLastContext.contains("function")
            ) {
                suggestions.add(DynamicPrompt("Add error handling to this code", "coding", "code"))
                suggestions.add(DynamicPrompt("Show complete working example", "coding", "code"))
                suggestions.add(DynamicPrompt("Explain coroutines lifecycle safety", "coding", "code"))
                return suggestions
            }

            // Context: Phone actions / intents
            if (combinedLastContext.contains("youtube") || combinedLastContext.contains("camera") ||
                combinedLastContext.contains("maps") || combinedLastContext.contains("timer")
            ) {
                suggestions.add(DynamicPrompt("Rahul, YouTube kholo", "action", "smart_display"))
                suggestions.add(DynamicPrompt("Rahul, camera kholo", "action", "photo_camera"))
                suggestions.add(DynamicPrompt("Rahul, 5 minute ka timer lagao", "action", "timer"))
                return suggestions
            }

            // Context: Bengali language discussion
            if (language == AssistantLanguage.BENGALI || combinedLastContext.contains("bangla") || combinedLastContext.contains("বাংলা")) {
                suggestions.add(DynamicPrompt("Banglay ektu shorol kore bolo", "translation", "translate"))
                suggestions.add(DynamicPrompt("Ekta sundor udahoron dao", "study", "lightbulb"))
                suggestions.add(DynamicPrompt("Erpor ki kora uchit?", "smart", "auto_awesome"))
                return suggestions
            }

            // Context: Hindi language discussion
            if (language == AssistantLanguage.HINDI || combinedLastContext.contains("hindi") || combinedLastContext.contains("हिंदी")) {
                suggestions.add(DynamicPrompt("इसे सरल हिंदी में समझाओ", "translation", "translate"))
                suggestions.add(DynamicPrompt("एक अच्छा उदाहरण दीजिए", "study", "lightbulb"))
                suggestions.add(DynamicPrompt("आगे क्या करना चाहिए?", "smart", "auto_awesome"))
                return suggestions
            }

            // Context: GF / Romantic Mode active
            if (personalityMode == PersonalityMode.GF_LOVE) {
                suggestions.add(DynamicPrompt("Tell me something sweet and loving", "love", "favorite"))
                suggestions.add(DynamicPrompt("How was your day, Rahul?", "love", "favorite"))
                suggestions.add(DynamicPrompt("Give me a cute romantic compliment", "love", "favorite"))
                suggestions.add(DynamicPrompt("Can you sing a song for me?", "love", "music_note"))
                return suggestions
            }

            // Standard intelligent conversational follow-ups
            suggestions.add(DynamicPrompt("Can you explain in simpler terms?", "study", "school"))
            suggestions.add(DynamicPrompt("Give me a practical real-world example", "study", "lightbulb"))
            suggestions.add(DynamicPrompt("What are the key takeaways?", "writing", "edit_note"))
            suggestions.add(DynamicPrompt("Translate this to Bengali & Hindi", "translation", "translate"))
            return suggestions
        }

        // 3. Initial Empty State Suggestions (Language & Mode adapted)
        return when (language) {
            AssistantLanguage.BENGALI -> listOf(
                DynamicPrompt("Rahul, YouTube kholo", "action", "smart_display"),
                DynamicPrompt("Banglay amar proshner uttor dao", "study", "translate"),
                DynamicPrompt("Ekta 10 minuter timer lagao", "action", "timer"),
                DynamicPrompt("Quantum computing shorol bhabe bojhao", "study", "school"),
                DynamicPrompt("Ekta sundor kobita ba golpo lekho", "writing", "edit_note")
            )
            AssistantLanguage.HINDI -> listOf(
                DynamicPrompt("Rahul AI, YouTube kholo", "action", "smart_display"),
                DynamicPrompt("Rahul AI, 10 minute ka timer lagao", "action", "timer"),
                DynamicPrompt("Rahul AI, camera kholo", "action", "photo_camera"),
                DynamicPrompt("Quantum computing aasan shabdon mein batao", "study", "school"),
                DynamicPrompt("Ek formal leave application likh do", "writing", "edit_note")
            )
            else -> listOf(
                DynamicPrompt("Rahul, open YouTube", "action", "smart_display"),
                DynamicPrompt("Set a 10 minute timer", "action", "timer"),
                DynamicPrompt("Open Google Maps", "action", "map"),
                DynamicPrompt("Explain quantum computing simply", "study", "school"),
                DynamicPrompt("Draft a polite follow-up email", "writing", "edit_note"),
                DynamicPrompt("Translate to Bengali and Hindi", "translation", "translate")
            )
        }
    }
}
