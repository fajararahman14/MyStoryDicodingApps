package com.fajar.mystorydicodingapps.di

import android.content.Context
import com.fajar.mystorydicodingapps.StoryRepository
import com.fajar.mystorydicodingapps.network.ApiConfig

object Injection {
    fun provideRepository(): StoryRepository{
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }
}