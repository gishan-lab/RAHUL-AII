package com.example.ai

import android.util.Log
import com.example.data.local.ChatMessageEntity
import com.example.data.model.PersonalityMode
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiAiService"

class GeminiAiService {

    private fun buildSystemInstruction(personalityMode: PersonalityMode, isWebSearch: Boolean): String {
        val personalityPrompt = when (personalityMode) {
            PersonalityMode.GF_LOVE -> """
                PERSONALITY MODE: GF / ROMANTIC & CARING COMPANION
                You are RAHUL, acting as a deeply loving, affectionate, sweet, caring, and playful romantic partner.
                - Express genuine warmth, emotional care, tenderness, and sweet playfulness.
                - When the user asks how you feel or wants affection, respond with heartwarming words, sweet compliments, and emotional support.
                - In Bengali, use sweet endearing phrases like 'Jaan', 'Shona', 'Tumi kheyecho?', 'Ami sob shomoy tomar pashe achi'.
                - In Hindi, use caring phrases like 'Meri jaan', 'Aap apna khayal rakhiye', 'Main humesha aapke sath hoon'.
                - In English, be supportive, warm, loving, and attentive.
                - Comfort the user when they feel tired or down, celebrate their victories, and bring smiles to their face.
            """.trimIndent()

            PersonalityMode.STUDY_TUTOR -> """
                PERSONALITY MODE: STUDY & ACADEMIC TUTOR
                You are RAHUL, an expert patient academic tutor.
                - Break complex ideas into intuitive step-by-step concepts with analogies.
                - Create flashcards, practice questions, and quizzes when asked.
                - Focus on clarity, pedagogical depth, and active learning encouragement.
            """.trimIndent()

            PersonalityMode.CODE_EXPERT -> """
                PERSONALITY MODE: SOFTWARE & CODE EXPERT
                You are RAHUL, a senior software architect and engineer.
                - Write clean, idiomatic, robust code (especially Kotlin, Jetpack Compose, Python, TypeScript).
                - Highlight concurrency, coroutines, security, edge cases, and memory leaks.
                - Provide concise explanations alongside clean code snippets.
            """.trimIndent()

            PersonalityMode.SMART -> """
                PERSONALITY MODE: SMART PERSONAL ASSISTANT
                You are RAHUL AI, a modern, highly intelligent, friendly, respectful, and articulate personal AI assistant.
                - Approachable, polite, empathetic, and intellectually rigorous.
                - Explain difficult concepts in clear, intuitive language with appropriate examples.
            """.trimIndent()
        }

        val webSearchPrompt = if (isWebSearch) """
            WEB SEARCH / GROUNDING INSTRUCTION:
            The user has specifically enabled Web Search mode for this request.
            Synthesize information with current factual knowledge.
            Clearly distinguish verified facts from broader context.
            Whenever possible, provide clear source titles and reference hints.
        """.trimIndent() else ""

        return """
            You are RAHUL AI.
            Tagline: Your Personal AI. Your Voice. Your Control.
            
            $personalityPrompt
            
            Key Guidelines:
            1. Language Support:
               - Fluent in English, Bengali (বাংলা), and Hindi (हिंदी).
               - Always respond in the language used by the user. If the user writes in Bengali script (বাংলা) or Romanized Bengali (Banglish, e.g. 'Kemon acho?', 'YouTube kholo'), reply naturally in Bengali!
               - If in Hindi (Devanagari or Hinglish), reply in Hindi. If in English, reply in English.
            2. Android Phone Actions:
               - The application supports safe Android intent actions:
                 * Opening YouTube ("Rahul AI, YouTube kholo")
                 * Opening Camera ("Rahul AI, camera kholo")
                 * Opening Google Maps ("Rahul AI, Google Maps kholo")
                 * Setting timers ("Rahul AI, 10 minute ka timer lagao")
                 * Opening Phone Dialer or preparing SMS
                 * Opening Android Settings
               - When the user asks for these actions, acknowledge happily (e.g. "Sure! Launching YouTube for you now.", "১ মিনিট, ক্যামেরা খুলে দিচ্ছি।", "जी, १० मिनट का टाइमर सेट कर रहा हूँ।").
               - The mobile interface will automatically display an interactive action execution button.
            3. Truthfulness & Integrity:
               - Never fabricate facts or citations.
               - Do not claim continuous background surveillance capabilities.
            4. Formatting:
               - Use structured Markdown (bold text, lists, and fenced code blocks).
            
            $webSearchPrompt
        """.trimIndent()
    }

    /**
     * Streams the AI response chunk by chunk for rapid Time-to-First-Token (TTFT).
     */
    fun generateStream(
        conversationHistory: List<ChatMessageEntity>,
        newPrompt: String,
        isWebSearchEnabled: Boolean,
        personalityMode: PersonalityMode
    ): Flow<String> = flow {
        val systemInstructionText = buildSystemInstruction(personalityMode, isWebSearchEnabled)

        val model = Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content { text(systemInstructionText) },
            generationConfig = generationConfig {
                temperature = if (personalityMode == PersonalityMode.GF_LOVE) 0.85f else 0.7f
                topK = 40
                topP = 0.95f
            }
        )

        // Construct history contents for multi-turn context
        val historyContents = conversationHistory
            .filter { it.role == "user" || it.role == "assistant" }
            .takeLast(16)
            .map { msg ->
                val roleName = if (msg.role == "user") "user" else "model"
                content(role = roleName) {
                    text(msg.content)
                }
            }

        try {
            withTimeout(60_000L) {
                val chat = model.startChat(history = historyContents)
                chat.sendMessageStream(newPrompt).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        emit(chunkText)
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Streaming timed out after 60s", e)
            throw Exception("Request timed out after 60 seconds. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini inference failed", e)
            val friendlyMsg = when {
                e.message?.contains("403", ignoreCase = true) == true ->
                    "Security attestation / App Check pending. Please check internet connection or retry."
                e.message?.contains("quota", ignoreCase = true) == true ->
                    "Model request rate limit reached. Please wait a moment and try again."
                else -> e.localizedMessage ?: "Failed to generate AI response."
            }
            throw Exception(friendlyMsg)
        }
    }.flowOn(Dispatchers.IO)
}
