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

        suspend fun sendMessage(
                userInput: String,
                chatHistory: List<ChatMessage>,
                conversationId: Long
        ): Long {
                var currentConversationId = conversationId
                if (conversationId == -1L) {
                        // 新建对话
                        val newConversation =
                                Conversation(
                                        title = userInput,
                                        lastMessageTimestamp = System.currentTimeMillis()
                                )
                        currentConversationId = chatMessageDao.insertConversation(newConversation)
                }

                try {
                        // 立刻将用户的消息存入数据库，让 UI 立即显示
                        val userMessageEntity =
                                ChatMessageEntity(
                                        conversationId = currentConversationId,
                                        content = userInput,
                                        author = Author.USER.name
                                )
                        chatMessageDao.insertMessage(userMessageEntity)

                        // 准备要发送给 API 的历史记录
                        // 首先，过滤掉之前所有的错误信息和被拦截的提示
                        var historyForApi: List<ChatMessage> =
                                chatHistory.filter {
                                        !it.content.startsWith("Error:") &&
                                                !it.content.startsWith("喵...")
                                }

                        // 如果过滤后历史记录为空，说明是新对话，就在最前面加上系统设定
                        if (historyForApi.isEmpty()) {
                                val systemPromptMessage =
                                        ChatMessage(
                                                content = promptManager.getPrompt(),
                                                author = Author.MODEL
                                        )
                                historyForApi = listOf(systemPromptMessage)
                        }

                        // 将用户当前输入的消息也加入到待发送列表
                        val currentUserMessage =
                                ChatMessage(content = userInput, author = Author.USER)
                        historyForApi = historyForApi + currentUserMessage

                        // 只取最近的 30 条消息作为上下文，防止请求体过长
                        historyForApi = historyForApi.takeLast(30)

                        val contents =
                                historyForApi.map { message ->
                                        Content(
                                                role =
                                                        if (message.author == Author.USER) "user"
                                                        else "model",
                                                parts = listOf(Part(text = message.content))
                                        )
                                }

                        val request = GeminiRequest(contents = contents)
                        val response =
                                RetrofitClient.apiService.generateContent(
                                        request,
                                        apiKeyManager.getApiKey()
                                )

                        val modelResponseText =
                                response.candidates
                                        ?.firstOrNull()
                                        ?.content
                                        ?.parts
                                        ?.firstOrNull()
                                        ?.text

                        if (modelResponseText != null && modelResponseText.isNotEmpty()) {
                                // 将 AI 的有效回复存入数据库
                                val modelMessageEntity =
                                        ChatMessageEntity(
                                                conversationId = currentConversationId,
                                                content = modelResponseText,
                                                author = Author.MODEL.name
                                        )
                                chatMessageDao.insertMessage(modelMessageEntity)
                        } else {
                                // 如果回复被拦截或为空，存入一条提示信息
                                val blockedResponseEntity =
                                        ChatMessageEntity(
                                                conversationId = currentConversationId,
                                                content = "喵... 人家好像不知道该说什么了... (响应被拦截或为空)",
                                                author = Author.MODEL.name
                                        )
                                chatMessageDao.insertMessage(blockedResponseEntity)
                        }

                        chatMessageDao.updateConversation(
                                currentConversationId,
                                userInput,
                                System.currentTimeMillis()
                        )

                        return currentConversationId
                } catch (e: Exception) {
                        // 发生异常时，先存储错误消息
                        val errorMessageEntity =
                                ChatMessageEntity(
                                        conversationId = currentConversationId,
                                        content = "Error: ${e.message}",
                                        author = Author.MODEL.name
                                )
                        chatMessageDao.insertMessage(errorMessageEntity)

                        // 更新对话的最后修改时间
                        chatMessageDao.updateConversation(
                                currentConversationId,
                                userInput,
                                System.currentTimeMillis()
                        )

                        // 即使发生异常也返回正确的 conversationId
                        return currentConversationId
                }
        }

        suspend fun clearChatHistory(conversationId: Long) {
                chatMessageDao.clearMessagesForConversation(conversationId)
        }

        fun getAllConversations(): Flow<List<Conversation>> {
                return chatMessageDao.getAllConversations()
        }
}
