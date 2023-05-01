package com.fajar.mystorydicodingapps.network

import com.google.gson.annotations.SerializedName


data class ResponseRegister(
    @field:SerializedName("error")
    var error : Boolean,

    @field:SerializedName("message")
    var message : String,
)