package com.gaoshiqi.chatgpt

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface ChatGPTAPI {
    @POST("v1/chat/completions")
    fun sendMsg(@Body requestMsg: RequestBody): Call<ChatGPTResponseData>
}