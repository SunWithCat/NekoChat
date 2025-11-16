package com.sunwithcat.nekochat.data.local

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri

class AvatarManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("avatar_prefs", Context.MODE_PRIVATE)
    private val contentResolver = context.contentResolver

    companion object {
        private const val KEY_USER_AVATAR_URI = "user_avatar_uri"
        private const val KEY_MODEL_AVATAR_URI = "model_avatar_uri"
    }

    private fun getValidAvatarUri(uriString: String?): String? {
        if (uriString.isNullOrBlank()) return null
        return try {
            contentResolver.openInputStream(uriString.toUri())?.use {
                // 可以正常打开
            }
            uriString
        } catch (e: Exception) {
            println("头像路径出错：$e")
            null
        }
    }

    // 保存 URI 获取永久读取权限
    private fun saveUri(key: String, uri: Uri?) {
        val uriString = uri?.toString()
        if (uriString != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                prefs.edit().putString(key, uriString).apply()
            } catch (e: SecurityException) {
                e.printStackTrace()
                // 处理权限获取失败的情况
                Toast.makeText(context, "获取失败", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 如果 URI 为null， 清除旧的 URI
            prefs.edit().remove(key).apply()
        }
    }

    // 获取保存的 URI 字符串
    private fun getUriString(key: String): String? {
        return prefs.getString(key, null)
    }

    // 用户头像
    fun saveUserAvatar(uri: Uri?) {
        saveUri(KEY_USER_AVATAR_URI, uri)
    }

    fun getUserAvatarUriString(): String? {
        val raw = getUriString(KEY_USER_AVATAR_URI)
        return getValidAvatarUri(raw)
    }

    // AI 头像
    fun saveModelAvatar(uri: Uri?) {
        saveUri(KEY_MODEL_AVATAR_URI, uri)
    }

    fun getModelAvatarUriString(): String? {
        val raw = getUriString(KEY_MODEL_AVATAR_URI)
        return getValidAvatarUri(raw)
    }
}