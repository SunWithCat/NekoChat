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

    @Query(
            "SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC"
    )
    fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun clearMessagesForConversation(conversationId: Long)

    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversation(conversationId: Long)

    @Insert suspend fun insertConversation(conversation: Conversation): Long

    @Query("SELECT * FROM conversations ORDER BY lastMessageTimestamp DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query(
            "UPDATE conversations SET title = :title, lastMessageTimestamp = :timestamp WHERE id = :conversationId"
    )
    suspend fun updateConversation(conversationId: Long, title: String, timestamp: Long)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: Long)

    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: Long): Conversation?

    @Query("UPDATE conversations SET customSystemPrompt = :prompt, customTemperature = :temp, customHistoryLength = :length WHERE id = :id")
    suspend fun updateConversationConfig(id: Long, prompt: String?, temp: Float?, length: Int?)

    @Query("UPDATE conversations SET title = :title WHERE id = :conversationId")
    suspend fun updateConversationTitle(conversationId: Long, title: String)
}
