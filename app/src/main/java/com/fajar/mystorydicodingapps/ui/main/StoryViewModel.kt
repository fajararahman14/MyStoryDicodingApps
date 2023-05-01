package com.fajar.mystorydicodingapps.ui.main

import androidx.lifecycle.ViewModel
import com.fajar.mystorydicodingapps.StoryRepository

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getAllStories(token: String) = storyRepository.getAllStories(token)

}