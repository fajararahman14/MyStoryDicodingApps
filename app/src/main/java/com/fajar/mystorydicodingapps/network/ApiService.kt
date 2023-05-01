package com.fajar.mystorydicodingapps.network


import com.fajar.mystorydicodingapps.network.story.StoryResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    @FormUrlEncoded
    fun doRegister(
        @Field("name") name : String,
        @Field("email") email : String,
        @Field("password") password : String
    ): Call<ResponseRegister>

    @POST("login")
    @FormUrlEncoded
    fun doLogin(
        @Field("email") email : String,
        @Field("password") password : String
    ): Call<ResponseLogin>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token : String,
        @Query("page") page : Int?,
        @Query("size") size : Int?,
    ): Call<StoryResponse>
}