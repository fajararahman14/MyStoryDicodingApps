package com.fajar.mystorydicodingapps.network


import com.fajar.mystorydicodingapps.network.story.DetailStoryResponse
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

    @GET("stories?location=1")
    fun getStoryLocation(
        @Header("Authorization") token: String,
        @Query("size") size: Int = 100
    ) : Call<StoryResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token : String,
        @Part photo : MultipartBody.Part,
        @Part("description") description : RequestBody,
        @Part("lat") lat : Float?,
        @Part("lon") lon : Float?,
    ): Call<UploadFileResponse>
}