package com.fajar.mystorydicodingapps.ui.login

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.fajar.mystorydicodingapps.data.entity.EntityUser
import com.fajar.mystorydicodingapps.data.local.datastore.UserPreference
import com.fajar.mystorydicodingapps.network.ApiConfig
import com.fajar.mystorydicodingapps.network.ResponseLogin
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(private val preferences: UserPreference) : ViewModel() {


    var loading = MutableLiveData(View.GONE)

    val error = MutableLiveData("")

    val loginResult = MutableLiveData<ResponseLogin>()


    fun login(email: String, password: String) {

        loading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().doLogin(email, password)
        client.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(
                call: Call<ResponseLogin>,
                response: Response<ResponseLogin>
            ) {

                when (response.code()) {

                    400 -> error.postValue("error")
                    401 -> error.postValue("E-mail address is not registered / Wrong E-mail")
                    200 -> {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            loginResult.postValue(response.body())
                            saveUser(
                                EntityUser(
                                    responseBody.loginResult.name,
                                    responseBody.loginResult.token,
                                    isLogin = true
                                )
                            )
                        } else {
                            error.postValue("Response body is null")
                        }

                    }
                    else -> error.postValue("Error ${response.code()} : ${response.errorBody()}")
                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e("Failure", t.message.toString())
                error.postValue(t.message)
            }
        })
    }


    fun saveUser(user: EntityUser) {
        viewModelScope.launch {
            preferences.saveUser(user)
        }
    }

    fun getUser(): LiveData<EntityUser> =
        preferences.getUser().asLiveData()

}