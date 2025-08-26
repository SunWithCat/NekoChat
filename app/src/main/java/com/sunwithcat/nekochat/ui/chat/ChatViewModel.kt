package com.sunwithcat.nekochat.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunwithcat.nekochat.data.model.Author
import com.sunwithcat.nekochat.data.model.ChatMessage
import com.sunwithcat.nekochat.data.repository.ChatRepository
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatUiState(val isModelProcessing: Boolean = false)

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _isModelProcessing = MutableStateFlow(false)

    private val _chatHistory: StateFlow<List<ChatMessage>> =
            chatRepository
                    .getChatHistory()
                    // 将 Flow 转换为 StateFlow，可以在 ViewModel 的生命周期内被安全地观察
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000), // 5秒后如果没有观察者就停止
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
    fun sendMessage(userInput: String) {
        if (_isModelProcessing.value || userInput.isBlank()) {
            return
        }

        viewModelScope.launch {
            _isModelProcessing.value = true
            // 只需调用 repository 的方法，UI 会通过 Flow 自动更新
            chatRepository.sendMessage(userInput, _chatHistory.value)
            _isModelProcessing.value = false
        }
    }
}
