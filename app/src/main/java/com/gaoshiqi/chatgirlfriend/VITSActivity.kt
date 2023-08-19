package com.gaoshiqi.chatgirlfriend

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.chatwaifu.vits.utils.SoundGenerateHelper
import com.chatwaifu.vits.utils.file.copyAssets2Local
import com.chatwaifu.vits.utils.permission.PermissionUtils
import com.gaoshiqi.chatgirlfriend.application.ChatGirlFriendApplication
import com.gaoshiqi.chatgirlfriend.utils.VITSManager

import java.io.File

class VITSActivity : AppCompatActivity() {

    private val vitsManager: VITSManager by lazy {
        VITSManager(this)
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vitsactivity)

        vitsManager.initVITS()

        val inputText = findViewById<EditText>(R.id.inputText)
        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            val message = inputText.text.toString().trim()
            Log.i("22222", message)
            vitsManager.generateSound(message)
        }
    }


}