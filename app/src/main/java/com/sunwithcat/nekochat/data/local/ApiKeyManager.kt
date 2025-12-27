package com.sunwithcat.nekochat.data.local

import android.content.Context

enum class ApiProvider {
    GOOGLE,
    OPENAI
}

class ApiKeyManager(context: Context) {
    private val prefs = context.getSharedPreferences("api_key_prefs",Context.MODE_PRIVATE)

    companion object {
        private const val KEY_API_KEY = "gemini_key_api"
        private const val KEY_PROVIDER = "api_provider"
        private const val KEY_OPENAI_BASE_URL = "openai_base_url"
        private const val KEY_OPENAI_API_KEY = "openai_api_key"
        private const val KEY_OPENAI_MODEL = "openai_model"
    }

    fun saveApiKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY,apiKey).apply()
    }

    fun getApiKey(): String {
        return prefs.getString(KEY_API_KEY, "") ?: ""
    }

    fun saveProvider(provider: ApiProvider) {
        prefs.edit().putString(KEY_PROVIDER, provider.name).apply()
    }

    fun getProvider(): ApiProvider{
        val name = prefs.getString(KEY_PROVIDER, ApiProvider.GOOGLE.name)
        return try {
            ApiProvider.valueOf(name ?: ApiProvider.GOOGLE.name)
        } catch (e: Exception) {
            ApiProvider.GOOGLE
        }
    }

    // OpenAI URL
    fun saveOpenAIBaseUrl(url: String) {
        prefs.edit().putString(KEY_OPENAI_BASE_URL, url).apply()
    }

    fun getOpenAIBaseUrl(): String {
        return prefs.getString(KEY_OPENAI_BASE_URL, "") ?: ""
    }

    // OpenAI Key
    fun saveOpenAIApiKey(apiKey: String) {
        prefs.edit().putString(KEY_OPENAI_API_KEY, apiKey).apply()
    }

    fun getOpenAIApiKey(): String {
        return prefs.getString(KEY_OPENAI_API_KEY, "") ?: ""
    }

    // OpenAI Model
    fun saveOpenAIModel(model: String) {
        prefs.edit().putString(KEY_OPENAI_MODEL, model).apply()
    }

    fun getOpenAIModel(): String {
        return prefs.getString(KEY_OPENAI_MODEL, "") ?: ""
    }
}