package com.sunwithcat.nekochat.data.model

// 消息的发送者
enum class Author {
    USER, MODEL
}

// 聊天消息的数据结构
data class ChatMessage(
    val content: String, // 消息内容
    val author: Author, // 消息作者 用户 or AI
    val isProcessing: Boolean = false // 是否正在处理中
)
