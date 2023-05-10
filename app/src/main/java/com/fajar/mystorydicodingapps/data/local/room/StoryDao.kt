package com.fajar.mystorydicodingapps.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fajar.mystorydicodingapps.network.story.StoryItem

@Dao
interface StoryDao {

    @Query("SELECT * FROM story")
    fun getStories() : PagingSource<Int, StoryItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStory(stories: List<StoryItem>)

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}