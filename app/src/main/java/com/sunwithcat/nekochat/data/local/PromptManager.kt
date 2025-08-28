package com.sunwithcat.nekochat.data.local

import android.content.Context
import com.sunwithcat.nekochat.data.model.AIConfig

class PromptManager(context: Context) {
    private val prefs = context.getSharedPreferences("prompt_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROMPT = "system_prompt"
    }

    fun savePrompt(prompt: String) {
        prefs.edit().putString(KEY_PROMPT, prompt).apply()
    }

    fun getPrompt(): String {
        return prefs.getString(KEY_PROMPT, AIConfig.DEFAULT_SYSTEM_PROMPT) ?: AIConfig.DEFAULT_SYSTEM_PROMPT
    }
}