package com.sunwithcat.nekochat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class Conversation(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val title: String,
        val lastMessageTimestamp: Long, // 最后一条消息时间戳
        val customSystemPrompt: String? = null, // 自定义提示词 (为空则使用全局配置)
        val customTemperature: Float? = null, // 自定义温度 (为空则使用全局配置)
        val customHistoryLength: Int? = null // 自定义历史长度 (为空则使用全局配置)
)
