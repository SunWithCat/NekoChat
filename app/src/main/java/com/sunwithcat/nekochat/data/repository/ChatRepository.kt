package com.sunwithcat.nekochat.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ChatRepository(
    private val chatMessageDao: ChatMessageDao,
    private val promptManager: PromptManager,
    private val apiKeyManager: ApiKeyManager,
    private val context: android.content.Context // 新增 Context
) {
    fun getChatHistory(conversationId: Long): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessagesForConversation(conversationId).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id.toString(),
                    content = entity.content,
                    author = Author.valueOf(entity.author),
                    isProcessing = false,
                    isError = entity.content.startsWith("Error:"),
                    imagePath = entity.imagePath
                )
            }
        }
    }

    suspend fun saveUserMessage(
        userInput: String,
        conversationId: Long,
        imagePath: String? = null
    ): Pair<Long, Long> {
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
                author = Author.USER.name,
                imagePath = imagePath
            )
        val messageId = chatMessageDao.insertMessage(userMessageEntity)
        return Pair(currentConversationId, messageId)
    }

    suspend fun updateMessageImage(messageId: Long, imagePath: String) {
        chatMessageDao.updateMessageImage(messageId, imagePath)
    }

    private fun ChatMessage.toGeminiContent(): Content {
        return Content(
            role = if (author == Author.USER) "user" else "model",
            parts = listOf(Part(text = content))
        )
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    suspend fun fetchModelResponse(
        userInput: String,
        chatHistory: List<ChatMessage>,
        conversationId: Long,
        base64Image: String? = null, // 新增可选参数：图片的 Base64 编码
        mimeType: String? = null // 新增可选参数：图片的 MIME 类型 (例如 "image/jpeg", "image/png")
    ) {
        // 在网络请求前检查
        if (apiKeyManager.getApiKey().isBlank()) {
            val apiKeyMissingError =
                ChatMessageEntity(
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
                    !it.content.startsWith("Error:") && !it.content.startsWith("喵...")
                }

            // 获取对话配置 (如果存在)
            val conversation = chatMessageDao.getConversationById(conversationId)

            // 确定最终使用的配置 (优先使用对话独立配置，否则使用全局配置)
            val systemPrompt =
                conversation?.customSystemPrompt?.takeIf { it.isNotBlank() }
                    ?: promptManager.getPrompt()
            val temperature = conversation?.customTemperature ?: promptManager.getTemperature()
            val historyLength = conversation?.customHistoryLength ?: promptManager.getLength()

            if (historyForApi.isEmpty()) {
                val systemPromptMessage = ChatMessage(content = systemPrompt, author = Author.MODEL)
                historyForApi = listOf(systemPromptMessage)
            }

            val currentUserMessage = ChatMessage(content = userInput, author = Author.USER)
            historyForApi = historyForApi + currentUserMessage
            // 上下文长度
            historyForApi = historyForApi.takeLast(historyLength)

            val contents = historyForApi.map { it.toGeminiContent() }.toMutableList()

            // 如果有图片，添加到最后一条用户消息中 (即当前消息)
            if (base64Image != null) {
                // 移除最后一条纯文本消息，替换为带图片的消息
                contents.removeLast()
                val imagePart =
                    Part(
                        inlineData =
                            com.sunwithcat.nekochat.data.remote.InlineData(
                                mimeType = mimeType
                                    ?: "image/jpeg", // 假设是 JPEG，实际应从
                                // Uri 获取
                                data = base64Image
                            )
                    )

                val parts =
                    if (userInput.isNotBlank()) {
                        val textPart = Part(text = userInput)
                        listOf(textPart, imagePart)
                    } else {
                        listOf(imagePart)
                    }

                contents.add(Content(role = "user", parts = parts))
            }

            val generationConfig = GenerationConfig(temperature = temperature)

            val request = GeminiRequest(contents = contents, generationConfig = generationConfig)
            val response =
                RetrofitClient.apiService.generateContent(request, apiKeyManager.getApiKey())

            val modelResponseText =
                response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""

            if (modelResponseText.isNotEmpty()) {
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
        } catch (e: retrofit2.HttpException) {
            // 语法解释: catch (e: Type) 用于捕获特定类型的异常。
            // 语法解释: when (value) 是 Kotlin 的 switch-case，非常强大。
            // e.code() 获取 HTTP 状态码。
            val errorMessage =
                when (e.code()) {
                    404 -> "Error: 404 Not Found. 喵? 找不到路了..." // 资源不存在
                    503 -> "Error: 503 Service Unavailable. 喵... 服务器累坏了..." // 服务器暂时不可用
                    429 -> "Error: 429 Too Many Requests. 喵! 慢点慢点!" // 请求太频繁
                    else -> "Error: ${e.message()}" // 其他错误，直接显示错误信息
                }

            // 创建一个错误消息实体，准备存入数据库
            val errorMessageEntity =
                ChatMessageEntity(
                    conversationId = conversationId,
                    content = errorMessage,
                    author = Author.MODEL.name
                )
            chatMessageDao.insertMessage(errorMessageEntity)
        } catch (e: Exception) {
            // 语法解释: 捕获所有其他类型的异常（比如网络断开、解析错误等）。
            // Exception 是所有异常的父类。
            val errorMessageEntity =
                ChatMessageEntity(
                    conversationId = conversationId,
                    content = "Error: ${e.message}",
                    author = Author.MODEL.name
                )
            chatMessageDao.insertMessage(errorMessageEntity)
        } finally {
            chatMessageDao.updateConversationTitleIfAuto(
                conversationId,
                userInput,
                System.currentTimeMillis()
            )
        }
    }

    suspend fun processImage(uri: android.net.Uri): Triple<String?, String?, String?> =
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val context = this@ChatRepository.context
                val contentResolver = context.contentResolver

                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it, null, options)
                }

                val maxDimension = 1024
                var inSampleSize = 1
                if (options.outHeight > maxDimension || options.outWidth > maxDimension) {
                    val halfHeight: Int = options.outHeight / 2
                    val halfWidth: Int = options.outWidth / 2
                    while ((halfHeight / inSampleSize) >= maxDimension &&
                        (halfWidth / inSampleSize) >= maxDimension
                    ) {
                        inSampleSize *= 2
                    }
                }

                val finalOptions =
                    BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
                val bitmap =
                    contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, finalOptions)
                    }
                if (bitmap == null) return@withContext Triple(null, null, null)

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val imageBytes = outputStream.toByteArray()

                // 释放 Bitmap
                bitmap.recycle()

                val fileName = "img_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, "chat_images")
                if (!file.exists()) file.mkdirs()
                val imageFile = File(file, fileName)
                FileOutputStream(imageFile).use { fos -> fos.write(imageBytes) }

                val base64 =
                    android.util.Base64.encodeToString(
                        imageBytes,
                        android.util.Base64.NO_WRAP
                    )

                Triple(imageFile.absolutePath, base64, "image/jpeg")
            } catch (e: Exception) {
                e.printStackTrace()
                Triple(null, null, null)
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

    suspend fun deleteMessage(messageId: String) {
        try {
            chatMessageDao.deleteMessageById(messageId.toLong())
        } catch (e: NumberFormatException) {
            println(e)
            // Ignore if id is not Long (e.g. temporary UUID)
        }
    }

    suspend fun updateConversationTitle(conversationId: Long, newTitle: String) {
        chatMessageDao.updateConversationTitle(conversationId, newTitle)
    }
}
