package com.sunwithcat.nekochat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// 定义了数据库中的表名
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) // 将 id 设置为主键，并自动生成
    val id: Long = 0,
    val conversationId: Long, // 标识属于哪次对话
    val content: String,
    val author: String, // 将 Author 枚举类型改为 String 类型，方便存储
    val timestamp: Long = System.currentTimeMillis(), // 新增时间戳，用于排序
    val imagePath: String? = null // 图片本地路径
)