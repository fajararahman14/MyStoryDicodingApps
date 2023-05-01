package com.fajar.mystorydicodingapps

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.fajar.mystorydicodingapps.network.ApiService
import com.fajar.mystorydicodingapps.network.story.StoryItem
import com.fajar.mystorydicodingapps.network.story.StoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
) {
    private val result = MediatorLiveData<Result<List<StoryItem>>>()

    fun getAllStories(token: String): LiveData<Result<List<StoryItem>>> {
        result.value = Result.Loading
        val client = apiService.getAllStories(token, null, null)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val story = response.body()?.listStory
                    val storiesList = ArrayList<StoryItem>()
                    story?.forEach { story ->
                        val stories = StoryItem(
                            story.id,
                            story.name,
                            story.description,
                            story.photoUrl,
                            story.createdAt,
                            story.lat,
                            story.lon,
                        )
                        storiesList.add(stories)
                    }
                    Log.d(TAG, "$storiesList")
                    Log.d(TAG, "Number of stories obtained: ${storiesList.size}")
                    result.value = Result.Success(storiesList)
                } else {
                    result.value = Result.Error(response.message())
                    Log.e(TAG, response.message())
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
                Log.e(TAG, t.message.toString())
            }
        })
        return result
    }
    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}