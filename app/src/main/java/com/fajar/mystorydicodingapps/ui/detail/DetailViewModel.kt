package com.fajar.mystorydicodingapps.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fajar.mystorydicodingapps.Result
import com.fajar.mystorydicodingapps.network.ApiConfig
import com.fajar.mystorydicodingapps.network.story.DetailStoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {

    private val _storyResult = MutableLiveData<Result<DetailStoryResponse>>()
    val storyResult: LiveData<Result<DetailStoryResponse>> = _storyResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun detailStory(token: String, id: String) {
        _isLoading.value = true

        ApiConfig.getApiService().getDetailStory("Bearer $token", id)
            .enqueue(object : Callback<DetailStoryResponse> {

                override fun onResponse(
                    call: Call<DetailStoryResponse>,
                    response: Response<DetailStoryResponse>
                ) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _storyResult.value = Result.Success(responseBody)
                        } else {
                            _storyResult.value = Result.Error("Response body is null")
                            Log.e(TAG, "onResponse : Response body is null")
                        }
                    } else {
                        _storyResult.value = Result.Error(response.message())
                        Log.e(TAG, "onFailure : ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                    _isLoading.value = false
                    _storyResult.value = Result.Error(t.message.toString())
                    Log.e(TAG, "onFailure : ${t.message.toString()}")
                }
            })
    }

    companion object {
        private const val TAG = "DetailViewModel"
    }
}