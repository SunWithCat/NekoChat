package com.sunwithcat.nekochat.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunwithcat.nekochat.data.model.Author
import com.sunwithcat.nekochat.data.model.ChatMessage
import com.sunwithcat.nekochat.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatUiState(
        val messages: List<ChatMessage> = emptyList(), // 聊天消息列表
        val isModelProcessing: Boolean = false // AI模型是否处理中
)

class ChatViewModel : ViewModel() {
    // 实例化Repository
    private val chatRepository = ChatRepository()

    // 创建一个可变的StateFlow来持有UI状态
    private val _uiState = MutableStateFlow(ChatUiState())
    // 不可变的StateFlow，只可观察
    val uiState = _uiState.asStateFlow()

    // 发送消息的方法
    fun sendMessage(userInput: String) {
        if (_uiState.value.isModelProcessing || userInput.isBlank()) {
            return
        }

        // 将用户的消息添加到UI状态中
        _uiState.update { currentState ->
            val userMessage = ChatMessage(content = userInput, author = Author.USER)
            currentState.copy(messages = currentState.messages + userMessage)
        }

        // 更新状态为处理中
        _uiState.update { currentState ->
            val loadingMessage =
                    ChatMessage(content = "...", author = Author.MODEL, isProcessing = true)
            currentState.copy(
                    isModelProcessing = true,
                    messages = currentState.messages + loadingMessage
            )
        }

        // 网络请求
        viewModelScope.launch {
            val historyToSend = _uiState.value.messages
                .filter { !it.isProcessing } // 过滤掉 ...
            // 调用方法获取结果
            val result = chatRepository.sendMessage(historyToSend)

            // 更新UI状态
            withContext(Dispatchers.Main) {
                result
                        .onSuccess { modelResponse ->
                            _uiState.update { currentState ->
                                val newMessages =
                                        currentState.messages.dropLast(1) +
                                                ChatMessage(
                                                        content = modelResponse,
                                                        author = Author.MODEL
                                                )
                                currentState.copy(messages = newMessages, isModelProcessing = false)
                            }
                        }
                        .onFailure { error ->
                            _uiState.update { currentState ->
                                val newMessages =
                                        currentState.messages.dropLast(1) +
                                                ChatMessage(
                                                        content = "Error: ${error.message}",
                                                        author = Author.MODEL
                                                )
                                currentState.copy(messages = newMessages, isModelProcessing = false)
                            }
                        }
            }
        }
    }
}
