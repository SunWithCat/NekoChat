package com.sunwithcat.nekochat.ui.settings

import androidx.lifecycle.ViewModel
import com.sunwithcat.nekochat.data.local.PromptManager

class SettingsViewModel(private val promptManager: PromptManager): ViewModel() {
    fun getCurrentPrompt(): String {
        return promptManager.getPrompt()
    }
    fun savePrompt(newPrompt: String) {
        promptManager.savePrompt(newPrompt)
    }
}