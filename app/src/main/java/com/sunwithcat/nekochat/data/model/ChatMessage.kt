package com.sunwithcat.nekochat.data.model

import java.util.UUID

// 消息的发送者
enum class Author {
    USER,
    MODEL
}

// 聊天消息的数据结构
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(), // 唯一ID
    val content: String, // 消息内容
    val author: Author, // 消息作者 用户 or AI
    val isProcessing: Boolean = false, // 是否正在处理中
    val isError: Boolean = false // 是否是错误消息
)
