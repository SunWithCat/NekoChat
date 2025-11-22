package com.sunwithcat.nekochat.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sunwithcat.nekochat.data.local.AppDatabase
import com.sunwithcat.nekochat.data.local.PromptManager

class SettingsViewModelFactory(
    private val context: Context,
    private val conversationId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val promptManager = PromptManager(context.applicationContext)
            val database = AppDatabase.getInstance(context.applicationContext)
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(promptManager, conversationId, database.chatMessageDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}