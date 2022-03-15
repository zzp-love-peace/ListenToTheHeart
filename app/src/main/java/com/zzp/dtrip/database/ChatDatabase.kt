package com.zzp.dtrip.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Chat::class], exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao

    companion object {
        private var instance: ChatDatabase? = null
        @Synchronized
        fun getDatabase(context: Context): ChatDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                ChatDatabase::class.java, "chat_database")
                .build().apply {
                    instance = this
                }
        }
    }

}