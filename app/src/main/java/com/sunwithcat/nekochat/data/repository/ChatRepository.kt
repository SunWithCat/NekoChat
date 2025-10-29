package com.sunwithcat.nekochat.data.repository

import com.sunwithcat.nekochat.data.local.ApiKeyManager
import com.sunwithcat.nekochat.data.local.ChatMessageDao
import com.sunwithcat.nekochat.data.local.PromptManager
import com.sunwithcat.nekochat.data.model.Author
import com.sunwithcat.nekochat.data.model.ChatMessage
import com.sunwithcat.nekochat.data.model.ChatMessageEntity
import com.sunwithcat.nekochat.data.model.Conversation
import com.sunwithcat.nekochat.data.remote.Content
import com.sunwithcat.nekochat.data.remote.GeminiRequest
import com.sunwithcat.nekochat.data.remote.GenerationConfig
import com.sunwithcat.nekochat.data.remote.Part
import com.sunwithcat.nekochat.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(
    private val chatMessageDao: ChatMessageDao,
    private val promptManager: PromptManager,
    private val apiKeyManager: ApiKeyManager
) {
    fun getChatHistory(conversationId: Long): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessagesForConversation(conversationId).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id.toString(),
                    content = entity.content,
                    author = Author.valueOf(entity.author),
                    isProcessing = false
                )
            }
        }
    }

    suspend fun saveUserMessage(userInput: String, conversationId: Long): Long {
        var currentConversationId = conversationId
        if (conversationId == -1L) {
            val newConversation =
                Conversation(
                    title = userInput,
                    lastMessageTimestamp = System.currentTimeMillis()
                )
            currentConversationId = chatMessageDao.insertConversation(newConversation)
        }
        val userMessageEntity =
            ChatMessageEntity(
                conversationId = currentConversationId,
                content = userInput,
                author = Author.USER.name
            )
        chatMessageDao.insertMessage(userMessageEntity)
        return currentConversationId
    }

    suspend fun fetchModelResponse(
        userInput: String,
        chatHistory: List<ChatMessage>,
        conversationId: Long
    ) {
        // 在网络请求前检查
        if (apiKeyManager.getApiKey().isBlank()) {
            val apiKeyMissingError = ChatMessageEntity(
                conversationId = conversationId,
                content = "喵~ 主人还没有设置 API Key 呐，去侧边栏设置一下吧！",
                author = Author.MODEL.name
            )
            chatMessageDao.insertMessage(apiKeyMissingError)
            return
        }

        try {
            var historyForApi: List<ChatMessage> =
                chatHistory.filter {
                    !it.content.startsWith("Error:") &&
                            !it.content.startsWith("喵...")
                }

            if (historyForApi.isEmpty()) {
                val systemPromptMessage =
                    ChatMessage(
                        content = promptManager.getPrompt(),
                        author = Author.MODEL
                    )
                historyForApi = listOf(systemPromptMessage)
            }

            val currentUserMessage =
                ChatMessage(content = userInput, author = Author.USER)
            historyForApi = historyForApi + currentUserMessage
            // 上下文长度
            val historyLines = promptManager.getLength()
            historyForApi = historyForApi.takeLast(historyLines)

            val contents =
                historyForApi.map { message ->
                    Content(
                        role =
                            if (message.author == Author.USER) "user"
                            else "model",
                        parts = listOf(Part(text = message.content))
                    )
                }

            val currentTemperature = promptManager.getTemperature()
            val generationConfig = GenerationConfig(temperature = currentTemperature)

            val request = GeminiRequest(contents = contents, generationConfig = generationConfig)
            val response =
                RetrofitClient.apiService.generateContent(
                    request,
                    apiKeyManager.getApiKey()
                )

            val modelResponseText =
                response.candidates
                    .firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

            if (modelResponseText != null && modelResponseText.isNotEmpty()) {
                val modelMessageEntity =
                    ChatMessageEntity(
                        conversationId = conversationId,
                        content = modelResponseText,
                        author = Author.MODEL.name
                    )
                chatMessageDao.insertMessage(modelMessageEntity)
            } else {
                val blockedResponseEntity =
                    ChatMessageEntity(
                        conversationId = conversationId,
                        content = "喵... 人家好像不知道该说什么了... (响应被拦截或为空)",
                        author = Author.MODEL.name
                    )
                chatMessageDao.insertMessage(blockedResponseEntity)
            }
        } catch (e: Exception) {
            val errorMessageEntity =
                ChatMessageEntity(
                    conversationId = conversationId,
                    content = "Error: ${e.message}",
                    author = Author.MODEL.name
                )
            chatMessageDao.insertMessage(errorMessageEntity)
        } finally {
            chatMessageDao.updateConversation(
                conversationId,
                userInput,
                System.currentTimeMillis()
            )
        }
    }

    suspend fun clearChatHistory(conversationId: Long) {
        chatMessageDao.clearMessagesForConversation(conversationId)
    }

    suspend fun deleteConversation(conversationId: Long) {
        chatMessageDao.deleteConversation(conversationId)
        chatMessageDao.clearMessagesForConversation(conversationId) // 确保删除所有关联消息
    }

    fun getAllConversations(): Flow<List<Conversation>> {
        return chatMessageDao.getAllConversations()
    }
}
