package com.sunwithcat.nekochat.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sunwithcat.nekochat.data.model.ChatMessageEntity
import com.sunwithcat.nekochat.data.model.Conversation

@Database(entities = [ChatMessageEntity::class, Conversation::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        // @Volatile 确保 INSTANCE 变量在所有线程中立即可见
        @Volatile
        private var INSTANCE: AppDatabase? = null

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
                            .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                            .fallbackToDestructiveMigration() // 清除旧数据 (仅当没有匹配的
                            // Migration 时)
                            .build()
                    INSTANCE = instance
                    instance
                }
        }
    }
}

val MIGRATION_2_3 =
    object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE conversations ADD COLUMN customSystemPrompt TEXT")
            db.execSQL("ALTER TABLE conversations ADD COLUMN customTemperature REAL")
            db.execSQL("ALTER TABLE conversations ADD COLUMN customHistoryLength INTEGER")
        }
    }

val MIGRATION_3_4 =
    object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE conversations ADD COLUMN isCustomTitle INTEGER NOT NULL DEFAULT 0")
        }
    }
