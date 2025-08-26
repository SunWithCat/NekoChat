package com.sunwithcat.nekochat.data.local // 新建一个 local 包来存放数据库相关代码

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sunwithcat.nekochat.data.model.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Insert suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages") suspend fun clearAllMessages()
}
