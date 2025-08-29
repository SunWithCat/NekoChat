package com.sunwithcat.nekochat.ui.settings

import androidx.lifecycle.ViewModel
import com.sunwithcat.nekochat.data.local.PromptManager
import com.sunwithcat.nekochat.data.model.AIConfig

class SettingsViewModel(private val promptManager: PromptManager): ViewModel() {
    fun getCurrentPrompt(): String {
        return promptManager.getPrompt()
    }
    fun savePrompt(newPrompt: String) {
        promptManager.savePrompt(newPrompt)
    }
    fun getDefaultPrompt(): String {
        return AIConfig.DEFAULT_SYSTEM_PROMPT
    }
}