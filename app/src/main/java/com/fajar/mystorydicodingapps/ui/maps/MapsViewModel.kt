package com.fajar.mystorydicodingapps.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fajar.mystorydicodingapps.network.story.StoryItem

class MapsViewModel : ViewModel() {

    private val _mapResult = MutableLiveData<List<StoryItem>>()
    val mapResult: LiveData<List<StoryItem>> = _mapResult

}