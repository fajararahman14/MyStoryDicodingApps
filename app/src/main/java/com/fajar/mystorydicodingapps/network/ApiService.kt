package com.fajar.mystorydicodingapps.network


import com.fajar.mystorydicodingapps.network.story.StoryResponse
import com.fajar.mystorydicodingapps.network.story.UploadFileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token : String,
        @Part photo : MultipartBody.Part,
        @Part("description") description : RequestBody,
    ): Call<UploadFileResponse>
}