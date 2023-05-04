package com.fajar.mystorydicodingapps.network.story

import com.google.gson.annotations.SerializedName

data class UploadFileResponse(
    @field:SerializedName("error")
    val error : Boolean,

    @field:SerializedName("message")
    val message : String
)
