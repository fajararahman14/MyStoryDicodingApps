package com.fajar.mystorydicodingapps.di

import android.content.Context
import com.fajar.mystorydicodingapps.StoryRepository
import com.fajar.mystorydicodingapps.data.local.room.StoryDatabase
import com.fajar.mystorydicodingapps.network.ApiConfig

object Injection {
    fun provideRepository(context : Context): StoryRepository{
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService, database)
    }
}