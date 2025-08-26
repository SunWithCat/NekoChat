package com.sunwithcat.nekochat.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sunwithcat.nekochat.data.model.ChatMessageEntity

@Database(entities = [ChatMessageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        // @Volatile 确保 INSTANCE 变量在所有线程中立即可见
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // synchronized 确保同一时间只有一个线程可以执行这段代码，避免重复创建实例
            return INSTANCE
                    ?: synchronized(this) {
                        val instance =
                                Room.databaseBuilder(
                                                context.applicationContext,
                                                AppDatabase::class.java,
                                                "neko_chat_database" // 数据库文件的名字
                                        )
                                        .build()
                        INSTANCE = instance
                        instance
                    }
        }
    }
}
