package com.example.data.repository

import com.example.data.model.QuickActionCategory
import com.example.data.model.SuggestedPrompt

object QuickActionsData {
    val categories = listOf(
        QuickActionCategory(
            id = "study",
            title = "Study",
            subtitle = "Summaries, flashcards & concept breakdowns",
            iconName = "school",
            prompts = listOf(
                SuggestedPrompt(
                    title = "Explain a Concept",
                    prompt = "Explain quantum computing and superposition in simple terms with everyday analogies.",
                    category = "study"
                ),
                SuggestedPrompt(
                    title = "Make Quick Flashcards",
                    prompt = "Create 5 high-yield study flashcards for photosynthesis with key terms and definitions.",
                    category = "study"
                ),
                SuggestedPrompt(
                    title = "Practice Quiz",
                    prompt = "Give me a 3-question multiple choice quiz on world geography with explanations for the correct answers.",
                    category = "study"
                )
            )
        ),
        QuickActionCategory(
            id = "writing",
            title = "Writing",
            subtitle = "Polished emails, essays & creative stories",
            iconName = "edit_note",
            prompts = listOf(
                SuggestedPrompt(
                    title = "Formal Follow-Up Email",
                    prompt = "Draft a polite, professional follow-up email after a job interview emphasizing excitement and gratitude.",
                    category = "writing"
                ),
                SuggestedPrompt(
                    title = "Grammar & Tone Check",
                    prompt = "Proofread and enhance this text for clarity and persuasive executive tone:\n\n[Paste text here]",
                    category = "writing"
                ),
                SuggestedPrompt(
                    title = "Creative Sci-Fi Story",
                    prompt = "Write a captivating short sci-fi story about an AI discovering a hidden frequency in deep space.",
                    category = "writing"
                )
            )
        ),
        QuickActionCategory(
            id = "translation",
            title = "Translation",
            subtitle = "English, Bengali (বাংলা) & Hindi (हिंदी)",
            iconName = "translate",
            prompts = listOf(
                SuggestedPrompt(
                    title = "Translate to Bengali & Hindi",
                    prompt = "Translate this phrase into both Bengali (বাংলা) and Hindi (हिंदी) with pronunciation guides:\n\n'Where can I find the nearest hospital and pharmacy?'",
                    category = "translation"
                ),
                SuggestedPrompt(
                    title = "Bengali Idioms to English",
                    prompt = "Explain the meaning and English equivalent of popular Bengali phrases like 'মেঘ না চাইতে জল' and 'গাছে কাঁঠাল গোঁফে তেল'.",
                    category = "translation"
                ),
                SuggestedPrompt(
                    title = "Hindi Business Greetings",
                    prompt = "Provide 5 professional Hindi greetings and opening phrases for a formal business letter or email.",
                    category = "translation"
                )
            )
        ),
        QuickActionCategory(
            id = "knowledge",
            title = "General Knowledge",
            subtitle = "Curious facts, history & science",
            iconName = "lightbulb",
            prompts = listOf(
                SuggestedPrompt(
                    title = "How Black Holes Work",
                    prompt = "How does an event horizon work, and what would happen if someone approached one?",
                    category = "knowledge"
                ),
                SuggestedPrompt(
                    title = "Ancient Trade Routes",
                    prompt = "What was the historical significance of the Silk Road and Maritime Silk Route?",
                    category = "knowledge"
                ),
                SuggestedPrompt(
                    title = "Science Mythbusters",
                    prompt = "Debunk 3 common scientific myths that many people still believe today.",
                    category = "knowledge"
                )
            )
        ),
        QuickActionCategory(
            id = "coding",
            title = "Coding",
            subtitle = "Architecture, bug fixing & algorithms",
            iconName = "code",
            prompts = listOf(
                SuggestedPrompt(
                    title = "Kotlin Coroutines vs Threads",
                    prompt = "Explain how Kotlin Coroutines achieve structured concurrency compared to traditional OS threads.",
                    category = "coding"
                ),
                SuggestedPrompt(
                    title = "Find & Fix the Bug",
                    prompt = "Review this snippet for potential memory leaks, performance bottlenecks, or concurrency bugs:\n\n[Paste code here]",
                    category = "coding"
                ),
                SuggestedPrompt(
                    title = "Clean Architecture Guide",
                    prompt = "Explain Clean Architecture in Android (Data, Domain, Presentation) with a practical sample project structure.",
                    category = "coding"
                )
            )
        )
    )

    val homeSuggestedPrompts = listOf(
        SuggestedPrompt(
            title = "Open YouTube App",
            prompt = "Rahul AI, YouTube kholo",
            category = "action",
            iconName = "smart_display"
        ),
        SuggestedPrompt(
            title = "Set 10 Min Timer",
            prompt = "Rahul AI, 10 minute ka timer lagao",
            category = "action",
            iconName = "timer"
        ),
        SuggestedPrompt(
            title = "Open Camera",
            prompt = "Rahul AI, camera kholo",
            category = "action",
            iconName = "photo_camera"
        ),
        SuggestedPrompt(
            title = "Bangla Conversation",
            prompt = "Rahul AI, Banglay amar proshner uttor dao: Artificial Intelligence kivabe kaj kore?",
            category = "translation",
            iconName = "translate"
        ),
        SuggestedPrompt(
            title = "Explain Quantum Computing",
            prompt = "Explain quantum computing in simple terms with everyday analogies.",
            category = "study",
            iconName = "school"
        ),
        SuggestedPrompt(
            title = "Draft Follow-Up Email",
            prompt = "Draft a polite and concise follow-up email after a project meeting.",
            category = "writing",
            iconName = "edit_note"
        )
    )
}
