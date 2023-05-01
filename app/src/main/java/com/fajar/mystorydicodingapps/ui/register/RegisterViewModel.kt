package com.fajar.mystorydicodingapps.ui.register

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fajar.mystorydicodingapps.network.ApiConfig
import com.fajar.mystorydicodingapps.network.ResponseRegister
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    val registerResult = MutableLiveData<ResponseRegister>()
    val loading = MutableLiveData(View.GONE)
    val error = MutableLiveData("")

    fun register(name: String, email: String, password: String) {
        loading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().doRegister(name, email, password)
        client.enqueue(object : Callback<ResponseRegister> {
            override fun onResponse(call: Call<ResponseRegister>, response: Response<ResponseRegister>) {
                when (response.code()) {
                    400 -> error.postValue("Error")
                    201 -> registerResult.postValue(response.body())
                    else -> error.postValue("Error ${response.code()}: ${response.errorBody()}")
                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e("Failure", t.message.toString())
                error.postValue(t.message)
            }
        })
    }
}






