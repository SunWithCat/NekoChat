package com.sunwithcat.nekochat.ui.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunwithcat.nekochat.data.model.Author
import com.sunwithcat.nekochat.data.model.ChatMessage
import com.sunwithcat.nekochat.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatUiState(val isModelProcessing: Boolean = false)

class ChatViewModel(private val chatRepository: ChatRepository, conversationId: Long) :
    ViewModel() {

    private val _currentConversationId = MutableStateFlow(conversationId)

    init {
        // 确保_currentConversationId正确初始化，即使值相同也强制更新
        _currentConversationId.value = conversationId
    }

    private val _isModelProcessing = MutableStateFlow(false)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val _chatHistory: StateFlow<List<ChatMessage>> =
        _currentConversationId
            .flatMapLatest { id ->
                if (id == -1L) {
                    flowOf(emptyList())
                } else {
                    chatRepository.getChatHistory(id)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // 将两个 Flow 合并成最终的 UI State Flow
    val uiState: StateFlow<ChatUiState> =
        combine(_isModelProcessing, _chatHistory) { isProcessing, _ ->
            ChatUiState(isModelProcessing = isProcessing)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ChatUiState()
            )

    // 将消息列表单独暴露给 UI
    val messages: StateFlow<List<ChatMessage>> =
        combine(_chatHistory, _isModelProcessing) { history, isProcessing ->
            if (isProcessing) {
                history +
                        ChatMessage(
                            id = UUID.randomUUID().toString(),
                            content = "...",
                            author = Author.MODEL,
                            isProcessing = true
                        )
            } else {
                history
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // 简化的 sendMessage 方法
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(userInput: String, imageUri: android.net.Uri? = null) {
        if (_isModelProcessing.value || (userInput.isBlank() && imageUri == null)) {
            return
        }

        viewModelScope.launch {
            _isModelProcessing.value = true

            // 如果有图片，先保存原始 Uri，让 UI 立即显示
            val initialImagePath = imageUri?.toString()
            val (conversationId, messageId) =
                chatRepository.saveUserMessage(
                    userInput,
                    _currentConversationId.value,
                    initialImagePath
                )

            if (_currentConversationId.value == -1L) {
                _currentConversationId.value = conversationId
            }

            var finalBase64: String? = null
            var finalMimeType: String? = null

            if (imageUri != null) {
                val (localPath, base64, mimeType) = chatRepository.processImage(imageUri)
                finalBase64 = base64
                finalMimeType = mimeType

                if (localPath != null) {
                    chatRepository.updateMessageImage(messageId, localPath)
                }
            }

            chatRepository.fetchModelResponse(
                userInput,
                _chatHistory.value,
                conversationId,
                finalBase64,
                finalMimeType
            )

            _isModelProcessing.value = false
        }
    }

    fun clearChatHistory() {
        if (_currentConversationId.value != -1L) {
            viewModelScope.launch { chatRepository.clearChatHistory(_currentConversationId.value) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun retry() {
        val history = _chatHistory.value
        if (history.isEmpty()) return

        val lastMessage = history.last()

        // 检查最后一条消息是否是 AI 发出的错误消息
        if (lastMessage.isError && lastMessage.author == Author.MODEL) {
            viewModelScope.launch {
                // 1. 删除错误消息
                // 调用 Repository 的挂起函数 (suspend function)
                chatRepository.deleteMessage(lastMessage.id)

                // 2. 找到上一条用户消息重新发送
                // 语法解释: findLast { ... } 从后往前查找符合条件的元素。
                // { it.author == Author.USER } 是一个 lambda 表达式，it 代表列表中的每一个元素。
                val lastUserMessage = history.findLast { it.author == Author.USER }

                if (lastUserMessage != null) {
                    // 设置状态为正在处理，UI 会显示加载动画
                    _isModelProcessing.value = true

                    // 重新调用获取响应的方法
                    chatRepository.fetchModelResponse(
                        lastUserMessage.content,
                        history,
                        _currentConversationId.value
                    )

                    // 处理完成，恢复状态
                    _isModelProcessing.value = false
                }
            }
        }
    }
}
