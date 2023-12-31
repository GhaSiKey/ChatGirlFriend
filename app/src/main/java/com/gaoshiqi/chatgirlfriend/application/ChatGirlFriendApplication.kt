package com.gaoshiqi.chatgirlfriend.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import java.io.File

class ChatGirlFriendApplication: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var baseAppDir: String
    }

    override fun onCreate() {
        super.onCreate()
        baseAppDir = File("${this.filesDir?.parentFile?.path}").absolutePath
        context = applicationContext
    }
}