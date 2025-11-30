package com.sunwithcat.nekochat.ui.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sunwithcat.nekochat.data.local.ApiKeyManager
import com.sunwithcat.nekochat.data.local.AppDatabase
import com.sunwithcat.nekochat.data.local.PromptManager
import com.sunwithcat.nekochat.data.repository.ChatRepository

class ChatViewModelFactory(private val context: Context, private val conversationId: Long) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            // 获取数据库 DAO
            val dao = AppDatabase.getInstance(context).chatMessageDao()
            // 创建 PromptManager 实例
            val promptManager = PromptManager(context.applicationContext)
            // 获取 ApiKeyManager 实例
            val apiKeyManager = ApiKeyManager(context.applicationContext)
            // 创建 Repository
            val repository =
                    ChatRepository(dao, promptManager, apiKeyManager, context.applicationContext)
            // 创建 ViewModel
            @Suppress("UNCHECKED_CAST") return ChatViewModel(repository, conversationId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
