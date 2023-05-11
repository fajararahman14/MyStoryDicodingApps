package com.fajar.mystorydicodingapps.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.fajar.mystorydicodingapps.StoryRepository

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getAllStories(token: String) = storyRepository.getsAllStories(token).cachedIn(viewModelScope)

}