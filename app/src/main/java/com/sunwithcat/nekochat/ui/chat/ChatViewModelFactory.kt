package com.sunwithcat.nekochat.ui.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sunwithcat.nekochat.data.local.AppDatabase
import com.sunwithcat.nekochat.data.repository.ChatRepository

class ChatViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            // 获取数据库 DAO
            val dao = AppDatabase.getInstance(context).chatMessageDao()
            // 创建 Repository
            val repository = ChatRepository(dao)
            // 创建 ViewModel
            @Suppress("UNCHECKED_CAST") return ChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
