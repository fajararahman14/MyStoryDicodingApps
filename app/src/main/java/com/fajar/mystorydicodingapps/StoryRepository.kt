package com.fajar.mystorydicodingapps

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.fajar.mystorydicodingapps.network.ApiService
import com.fajar.mystorydicodingapps.network.story.StoryItem
import com.fajar.mystorydicodingapps.network.story.StoryResponse
import com.fajar.mystorydicodingapps.network.story.UploadFileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
) {
    private val result = MediatorLiveData<Result<List<StoryItem>>>()
    val errorMessage = MutableLiveData<String>()

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

    fun addStory(token: String, imageFile: MultipartBody.Part, description: RequestBody) {
        val client = apiService.uploadStory(token, imageFile, description)
        client.enqueue(object : Callback<UploadFileResponse>{
            override fun onResponse(
                call: Call<UploadFileResponse>,
                response: Response<UploadFileResponse>
            ) {
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody != null){
                        if(!responseBody.error){
                            Log.d(TAG, response.message())
                        }
                    } else{
                        Log.e(TAG, response.message())
                    }
                }
            }

            override fun onFailure(call: Call<UploadFileResponse>, t: Throwable) {
                t.message?.let { Log.e(TAG, it) }
                errorMessage.postValue(t.message.toString())
            }

        })
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}