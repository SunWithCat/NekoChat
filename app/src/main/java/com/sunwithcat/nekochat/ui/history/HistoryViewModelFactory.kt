package com.sunwithcat.nekochat.ui.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sunwithcat.nekochat.data.local.ApiKeyManager
import com.sunwithcat.nekochat.data.local.AppDatabase
import com.sunwithcat.nekochat.data.local.PromptManager
import com.sunwithcat.nekochat.data.repository.ChatRepository

class HistoryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            val dao = AppDatabase.getInstance(context).chatMessageDao()
            val promptManager = PromptManager(context.applicationContext)
            val apiKeyManager = ApiKeyManager(context.applicationContext)
            val repository = ChatRepository(dao,promptManager,apiKeyManager)

            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("UNKNOWN ViewModel class")
    }
}