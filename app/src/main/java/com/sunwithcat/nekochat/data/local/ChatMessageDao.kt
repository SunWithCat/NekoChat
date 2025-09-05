package com.sunwithcat.nekochat.data.local // 新建一个 local 包来存放数据库相关代码

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sunwithcat.nekochat.data.model.ChatMessageEntity
import com.sunwithcat.nekochat.data.model.Conversation
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Insert suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun clearMessagesForConversation(conversationId: Long)

    @Insert
    suspend fun insertConversation(conversation: Conversation): Long

    @Query("SELECT * FROM conversations ORDER BY lastMessageTimestamp DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query("UPDATE conversations SET title = :title, lastMessageTimestamp = :timestamp WHERE id = :conversationId")
    suspend fun updateConversation(conversationId: Long, title: String, timestamp: Long)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages") suspend fun clearAllMessages()
}
