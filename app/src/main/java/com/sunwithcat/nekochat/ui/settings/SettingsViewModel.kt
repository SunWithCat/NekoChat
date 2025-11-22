package com.sunwithcat.nekochat.ui.settings

import androidx.lifecycle.ViewModel
import com.sunwithcat.nekochat.data.local.ChatMessageDao
import com.sunwithcat.nekochat.data.local.PromptManager
import com.sunwithcat.nekochat.data.model.AIConfig
import kotlinx.coroutines.runBlocking

class SettingsViewModel(
        private val promptManager: PromptManager,
        private val conversationId: Long,
        private val chatMessageDao: ChatMessageDao
) : ViewModel() {

    private var customSystemPrompt: String? = null
    private var customTemperature: Float? = null
    private var customHistoryLength: Int? = null

    init {
        if (conversationId != -1L) {
            runBlocking {
                val conversation = chatMessageDao.getConversationById(conversationId)
                customSystemPrompt = conversation?.customSystemPrompt
                customTemperature = conversation?.customTemperature
                customHistoryLength = conversation?.customHistoryLength
            }
        }
    }

    fun getCurrentPrompt(): String {
        return customSystemPrompt?.takeIf { it.isNotBlank() } ?: promptManager.getPrompt()
    }

    fun getDefaultPrompt(): String {
        return AIConfig.DEFAULT_SYSTEM_PROMPT
    }

    fun getCurrentLength(): Int {
        return customHistoryLength ?: promptManager.getLength()
    }

    fun getCurrentTemperature(): Float {
        return customTemperature ?: promptManager.getTemperature()
    }

    suspend fun saveSettings(prompt: String, temperature: Float, length: Int) {
        if (conversationId != -1L) {
            val conversation = chatMessageDao.getConversationById(conversationId)
            if (conversation != null) {
                chatMessageDao.updateConversationConfig(conversationId, prompt, temperature, length)
                customSystemPrompt = prompt
                customTemperature = temperature
                customHistoryLength = length
            }
        } else {
            promptManager.savePrompt(prompt)
            promptManager.saveTemperature(temperature)
            promptManager.saveLength(length)
        }
    }
}
