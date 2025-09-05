package com.sunwithcat.nekochat.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 创建conversations表
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS conversations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title TEXT NOT NULL,
                    lastMessageTimestamp INTEGER NOT NULL
                )
            """)

            // 创建临时的chat_messages表
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS chat_messages_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    conversationId INTEGER NOT NULL,
                    content TEXT NOT NULL,
                    author TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    FOREIGN KEY(conversationId) REFERENCES conversations(id) ON DELETE CASCADE
                )
            """)

            // 如果存在旧的chat_messages表，将数据迁移到新表
            database.execSQL("""
                INSERT OR IGNORE INTO conversations (title, lastMessageTimestamp)
                VALUES ('旧对话', ${System.currentTimeMillis()})
            """)

            // 获取刚插入的conversation id
            database.execSQL("""
                INSERT OR IGNORE INTO chat_messages_new (conversationId, content, author, timestamp)
                SELECT 1, content, author, ${System.currentTimeMillis()}
                FROM chat_messages
            """)

            // 删除旧表并重命名新表
            database.execSQL("DROP TABLE IF EXISTS chat_messages")
            database.execSQL("ALTER TABLE chat_messages_new RENAME TO chat_messages")
        }
    }

    val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
}
