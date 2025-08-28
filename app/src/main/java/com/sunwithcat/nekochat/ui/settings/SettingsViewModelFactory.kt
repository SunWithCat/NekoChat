package com.sunwithcat.nekochat.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sunwithcat.nekochat.data.local.PromptManager

class SettingsViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val promptManager = PromptManager(context.applicationContext)
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(promptManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}