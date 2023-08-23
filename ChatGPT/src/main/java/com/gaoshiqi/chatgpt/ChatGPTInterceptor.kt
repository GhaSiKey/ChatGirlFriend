package com.gaoshiqi.chatgpt

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ChatGPTInterceptor: Interceptor {
    var privateKey: String = "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"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(addHeader(request))
    }

    private fun addHeader(request: Request) = request.newBuilder().run {
        addHeader("Content-Type","application/json")
        addHeader("Authorization", "Bearer $privateKey")
        build()
    }
}