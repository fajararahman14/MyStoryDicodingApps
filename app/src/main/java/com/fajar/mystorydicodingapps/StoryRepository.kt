package com.fajar.mystorydicodingapps

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.fajar.mystorydicodingapps.data.StoryRemoteMediator
import com.fajar.mystorydicodingapps.data.local.room.StoryDatabase
import com.fajar.mystorydicodingapps.network.ApiService
import com.fajar.mystorydicodingapps.network.story.StoryItem
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
) {

    fun getsAllStories(token: String): Flow<PagingData<StoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(apiService, storyDatabase, token),
            pagingSourceFactory = { storyDatabase.storyDao().getStories() }
        ).flow
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService,storyDatabase)
            }.also { instance = it }
    }
}