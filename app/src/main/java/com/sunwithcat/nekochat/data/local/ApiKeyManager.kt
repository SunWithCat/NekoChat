package com.sunwithcat.nekochat.data.local

import android.content.Context

class ApiKeyManager(context: Context) {
    private val prefs = context.getSharedPreferences("api_key_prefs",Context.MODE_PRIVATE)

    companion object {
        private const val KEY_API_KEY = "gemini_key_api"
    }

    fun saveApiKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY,apiKey).apply()
    }

    fun getApiKey(): String {
        return prefs.getString(KEY_API_KEY, "") ?: ""
    }
}