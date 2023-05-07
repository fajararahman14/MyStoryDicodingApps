package com.fajar.mystorydicodingapps.ui.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fajar.mystorydicodingapps.Result
import com.fajar.mystorydicodingapps.network.ApiConfig
import com.fajar.mystorydicodingapps.network.story.UploadFileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel : ViewModel() {

    private val _addStoryResult = MutableLiveData<Result<UploadFileResponse>>()
    val addStoryResult: LiveData<Result<UploadFileResponse>> = _addStoryResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun addStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().uploadStory(
            "Bearer $token",
            photo, description, lat, lon
        )
        client.enqueue(object : Callback<UploadFileResponse> {
            override fun onResponse(
                call: Call<UploadFileResponse>,
                response: Response<UploadFileResponse>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _addStoryResult.value = Result.Success(responseBody)
                    }
                } else {
                    _addStoryResult.value = Result.Error(response.message())
                    Log.e(TAG, "onFailure : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UploadFileResponse>, t: Throwable) {
                _isLoading.value = false
                _addStoryResult.value = Result.Error(t.message.toString())
                Log.e(TAG, "onFailure : ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "AddStoryViewModel"
    }
}