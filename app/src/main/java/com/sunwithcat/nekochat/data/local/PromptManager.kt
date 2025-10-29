package com.sunwithcat.nekochat.data.local

import android.content.Context
import com.sunwithcat.nekochat.data.model.AIConfig

class PromptManager(context: Context) {
    private val prefs = context.getSharedPreferences("prompt_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROMPT = "system_prompt"
        private const val KEY_CHAT_LENGTH = "chat_length"
        private const val KEY_TEMPERATURE = "ai_temperature"
        private const val DEFAULT_TEMPERATURE = 0.7f
    }

    fun savePrompt(prompt: String) {
        prefs.edit().putString(KEY_PROMPT, prompt).apply()
    }

    fun getPrompt(): String {
        return prefs.getString(KEY_PROMPT, AIConfig.DEFAULT_SYSTEM_PROMPT)
            ?: AIConfig.DEFAULT_SYSTEM_PROMPT
    }

    fun saveLength(length: Int) {
        prefs.edit().putInt(KEY_CHAT_LENGTH, length).apply()
    }

    fun getLength(): Int {
        return prefs.getInt(KEY_CHAT_LENGTH, AIConfig.DEFAULT_CHAT_LENGTH)
    }

    fun saveTemperature(temperature: Float) {
        prefs.edit().putFloat(KEY_TEMPERATURE, temperature).apply()
    }

    fun getTemperature(): Float {
        return prefs.getFloat(KEY_TEMPERATURE, DEFAULT_TEMPERATURE)
    }
}