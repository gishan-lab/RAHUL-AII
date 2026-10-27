package com.example.data.repository

import com.example.ai.GeminiAiService
import com.example.data.local.ChatDao
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ConversationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatRepository(
    private val chatDao: ChatDao,
    private val aiService: GeminiAiService
) {
    val allConversations: Flow<List<ConversationEntity>> = chatDao.getAllConversations()

    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessagesForConversation(conversationId)
    }

    suspend fun createNewConversation(initialTitle: String = "New Conversation"): ConversationEntity =
        withContext(Dispatchers.IO) {
            val conv = ConversationEntity(
                id = UUID.randomUUID().toString(),
                title = initialTitle,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            chatDao.insertConversation(conv)
            conv
        }

    suspend fun getConversation(conversationId: String): ConversationEntity? =
        withContext(Dispatchers.IO) {
            chatDao.getConversationById(conversationId)
        }

    suspend fun renameConversation(conversationId: String, newTitle: String) =
        withContext(Dispatchers.IO) {
            chatDao.updateConversationTitle(conversationId, newTitle)
        }

    suspend fun deleteConversation(conversationId: String) =
        withContext(Dispatchers.IO) {
            chatDao.deleteConversation(conversationId)
        }

    suspend fun clearAllConversations() =
        withContext(Dispatchers.IO) {
            chatDao.deleteAllConversations()
        }

    suspend fun insertUserMessage(
        conversationId: String,
        content: String,
        isWebSearch: Boolean
    ): ChatMessageEntity = withContext(Dispatchers.IO) {
        val userMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = "user",
            content = content,
            timestamp = System.currentTimeMillis(),
            isWebSearchUsed = isWebSearch
        )
        chatDao.insertMessage(userMsg)

        // If conversation has default title, update it with an excerpt of the first prompt
        val currentConv = chatDao.getConversationById(conversationId)
        if (currentConv != null && (currentConv.title == "New Conversation" || currentConv.title.isBlank())) {
            val snippet = if (content.length > 32) content.take(30).trim() + "..." else content.trim()
            chatDao.updateConversationTitle(conversationId, snippet)
        } else if (currentConv != null) {
            chatDao.updateConversationTitle(conversationId, currentConv.title, System.currentTimeMillis())
        }

        userMsg
    }

    suspend fun saveAssistantMessage(
        conversationId: String,
        content: String,
        isWebSearch: Boolean,
        isError: Boolean = false
    ): ChatMessageEntity = withContext(Dispatchers.IO) {
        val assistantMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = if (isError) "error" else "assistant",
            content = content,
            timestamp = System.currentTimeMillis(),
            isWebSearchUsed = isWebSearch
        )
        chatDao.insertMessage(assistantMsg)
        assistantMsg
    }

    suspend fun deleteMessage(messageId: String) = withContext(Dispatchers.IO) {
        chatDao.deleteMessage(messageId)
    }

    suspend fun getRecentMessages(conversationId: String): List<ChatMessageEntity> =
        withContext(Dispatchers.IO) {
            chatDao.getMessagesSnapshot(conversationId)
        }

    fun streamAiResponse(
        conversationHistory: List<ChatMessageEntity>,
        prompt: String,
        isWebSearchEnabled: Boolean,
        personalityMode: com.example.data.model.PersonalityMode
    ): Flow<String> {
        return aiService.generateStream(conversationHistory, prompt, isWebSearchEnabled, personalityMode)
    }
}
