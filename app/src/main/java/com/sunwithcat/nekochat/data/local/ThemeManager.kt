package com.sunwithcat.nekochat.data.local

import android.content.Context

class ThemeManager(context: Context) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    companion object {
        private const val KEY_THEME_MODE = "theme_mode"

        const val MODE_SYSTEM = 0 // 跟随系统
        const val MODE_LIGHT = 1 // 浅色
        const val MODE_DARK = 2 // 深色
    }

    fun saveThemeMode(mode: Int) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
    }

    fun getThemeMode(): Int {
        return prefs.getInt(KEY_THEME_MODE, MODE_SYSTEM)
    }
}