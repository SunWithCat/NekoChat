package com.sunwithcat.nekochat.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunwithcat.nekochat.data.model.Author
import com.sunwithcat.nekochat.data.model.ChatMessage
import com.sunwithcat.nekochat.data.repository.ChatRepository
import java.util.UUID
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatUiState(val isModelProcessing: Boolean = false)

class ChatViewModel(private val chatRepository: ChatRepository, private val conversationId: Long) :
        ViewModel() {

    private val _currentConversationId = MutableStateFlow(conversationId)

    init {
        // 确保_currentConversationId正确初始化，即使值相同也强制更新
        _currentConversationId.value = conversationId
    }

    private val _isModelProcessing = MutableStateFlow(false)

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
    fun sendMessage(userInput: String) {
        if (_isModelProcessing.value || userInput.isBlank()) {
            return
        }

        viewModelScope.launch {
            _isModelProcessing.value = true
            val newConversationId =
                    chatRepository.sendMessage(
                            userInput,
                            _chatHistory.value,
                            _currentConversationId.value
                    )
            if (_currentConversationId.value == -1L) {
                _currentConversationId.value = newConversationId
            }
            _isModelProcessing.value = false
        }
    }

    fun clearChatHistory() {
        if (_currentConversationId.value != -1L) {
            viewModelScope.launch { chatRepository.clearChatHistory(_currentConversationId.value) }
        }
    }
}
