package com.sunwithcat.nekochat.data.repository

import com.sunwithcat.nekochat.BuildConfig // 导入 BuildConfig
import com.sunwithcat.nekochat.data.model.AIConfig
import com.sunwithcat.nekochat.data.model.Author
import com.sunwithcat.nekochat.data.model.ChatMessage
import com.sunwithcat.nekochat.data.remote.Content
import com.sunwithcat.nekochat.data.remote.GeminiRequest
import com.sunwithcat.nekochat.data.remote.Part
import com.sunwithcat.nekochat.data.remote.RetrofitClient

class ChatRepository {
    // 发送消息
    suspend fun sendMessage(chatHistory: List<ChatMessage>): Result<String> {
        return try {
            // 检查是否需要添加系统提示
            val systemPrompt = AIConfig.getSystemPromptIfNeeded()
            val fullHistory =
                    if (systemPrompt != null) {
                        listOf(ChatMessage(content = systemPrompt, author = Author.MODEL)) +
                                chatHistory
                    } else {
                        chatHistory
                    }

            // 构建请求体
            val contents =
                    fullHistory.map { message ->
                        Content(
                                role = if (message.author == Author.USER) "user" else "model",
                                parts = listOf(Part(text = message.content))
                        )
                    }

            val request = GeminiRequest(contents = contents)

            // 发起网络请求
            val response =
                    RetrofitClient.apiService.generateContent(
                            request,
                            BuildConfig.GEMINI_API_KEY // 从 BuildConfig 获取 API Key
                    )

            // 解析响应
            val modelResponse =
                    response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            if (modelResponse.isNotEmpty()) {
                Result.success(modelResponse) // 成功返回包含AI回复的Success结果
            } else {
                Result.failure(Exception("Null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
