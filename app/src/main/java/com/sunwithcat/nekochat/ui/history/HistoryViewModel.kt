package com.sunwithcat.nekochat.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunwithcat.nekochat.data.model.Conversation
import com.sunwithcat.nekochat.data.repository.ChatRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(private val chatRepository: ChatRepository) : ViewModel() {
    val conversations: StateFlow<List<Conversation>> =
        chatRepository.getAllConversations()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}