package com.fajar.mystorydicodingapps.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fajar.mystorydicodingapps.data.entity.RemoteKeys
import com.fajar.mystorydicodingapps.network.story.StoryItem

@Database(entities = [StoryItem::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao() : StoryDao

    abstract fun remoteKeysDao() : RemoteKeyDao

    companion object {
        @Volatile
        private var instance: StoryDatabase? = null

        fun getInstance(context: Context) : StoryDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "Story.db"
                ).build()
            }
    }
}