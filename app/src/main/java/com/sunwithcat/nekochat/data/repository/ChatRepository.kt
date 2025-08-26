package com.sunwithcat.nekochat.data.repository

import com.sunwithcat.nekochat.BuildConfig
import com.sunwithcat.nekochat.data.local.ChatMessageDao
import com.sunwithcat.nekochat.data.model.AIConfig
import com.sunwithcat.nekochat.data.model.Author
import com.sunwithcat.nekochat.data.model.ChatMessage
import com.sunwithcat.nekochat.data.model.ChatMessageEntity
import com.sunwithcat.nekochat.data.remote.Content
import com.sunwithcat.nekochat.data.remote.GeminiRequest
import com.sunwithcat.nekochat.data.remote.Part
import com.sunwithcat.nekochat.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(private val chatMessageDao: ChatMessageDao) {

    fun getChatHistory(): Flow<List<ChatMessage>> {
        return chatMessageDao.getAllMessages().map { entities ->
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

    suspend fun sendMessage(userInput: String, chatHistory: List<ChatMessage>) {
        // 1. 立刻将用户的消息存入数据库，让 UI 立即显示
        val userMessageEntity = ChatMessageEntity(content = userInput, author = Author.USER.name)
        chatMessageDao.insertMessage(userMessageEntity)

        try {
            // 准备要发送给 API 的历史记录
            // 首先，过滤掉之前所有的错误信息和被拦截的提示
            var historyForApi: List<ChatMessage> =
                    chatHistory.filter {
                        !it.content.startsWith("Error:") && !it.content.startsWith("喵...")
                    }

            // 如果过滤后历史记录为空，说明是新对话，就在最前面加上系统设定
            if (historyForApi.isEmpty()) {
                val systemPromptMessage =
                        ChatMessage(content = AIConfig.DEFAULT_SYSTEM_PROMPT, author = Author.MODEL)
                historyForApi = listOf(systemPromptMessage)
            }

            // 将用户当前输入的消息也加入到待发送列表
            val currentUserMessage = ChatMessage(content = userInput, author = Author.USER)
            historyForApi = historyForApi + currentUserMessage

            // 只取最近的 10 条消息作为上下文，防止请求体过长
            historyForApi = historyForApi.takeLast(10)

            val contents =
                    historyForApi.map { message ->
                        Content(
                                role = if (message.author == Author.USER) "user" else "model",
                                parts = listOf(Part(text = message.content))
                        )
                    }

            val request = GeminiRequest(contents = contents)
            val response =
                    RetrofitClient.apiService.generateContent(request, BuildConfig.GEMINI_API_KEY)

            val modelResponseText =
                    response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (modelResponseText != null && modelResponseText.isNotEmpty()) {
                // 将 AI 的有效回复存入数据库
                val modelMessageEntity =
                        ChatMessageEntity(content = modelResponseText, author = Author.MODEL.name)
                chatMessageDao.insertMessage(modelMessageEntity)
            } else {
                // 如果回复被拦截或为空，存入一条提示信息
                val blockedResponseEntity =
                        ChatMessageEntity(
                                content = "喵... 人家好像不知道该说什么了... (响应被拦截或为空)",
                                author = Author.MODEL.name
                        )
                chatMessageDao.insertMessage(blockedResponseEntity)
            }
        } catch (e: Exception) {
            // 捕获到其他异常时，将异常信息存入数据库
            val errorMessageEntity =
                    ChatMessageEntity(content = "Error: ${e.message}", author = Author.MODEL.name)
            chatMessageDao.insertMessage(errorMessageEntity)
        }
    }
}
