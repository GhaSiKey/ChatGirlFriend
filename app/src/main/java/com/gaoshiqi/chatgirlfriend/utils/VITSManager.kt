package com.gaoshiqi.chatgirlfriend.utils

import android.content.Context
import android.util.Log
import com.chatwaifu.vits.utils.SoundGenerateHelper
import com.chatwaifu.vits.utils.file.copyAssets2Local
import com.chatwaifu.vits.utils.permission.PermissionUtils
import com.gaoshiqi.chatgirlfriend.MainActivity
import com.gaoshiqi.chatgirlfriend.VITSActivity
import com.gaoshiqi.chatgirlfriend.application.ChatGirlFriendApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class VITSManager(val context: Context) {

    private val vitsHelper: SoundGenerateHelper by lazy {
        SoundGenerateHelper(ChatGirlFriendApplication.context)
    }

    val finalModelList = mutableListOf<ModelList>()

    fun initVITS() {
        // 复制vits模型到手机本地并保存地址
        val vitsModelList = context.assets.list("VITSModels")  // ["ATRI", "Amadeus", "Yuuka"]
        Logi("des: " + ChatGirlFriendApplication.baseAppDir + File.separator)
        vitsModelList?.forEach { vitsName ->
            val srcPath = "VITSModels" + File.separator + vitsName
            Logi("src:$srcPath")
            ChatGirlFriendApplication.context.copyAssets2Local(
                deleteIfExists = true,
                srcPath = srcPath,
                desPath = ChatGirlFriendApplication.baseAppDir + File.separator
            ){
                    isSuccess: Boolean, absPath: String ->
                if (!isSuccess) {
                    Logi("copy vits $vitsName to disk failed....")
                    return@copyAssets2Local
                }
                finalModelList.add(
                    ModelList(
                        characterName = vitsName,
                        characterVitsPath = ChatGirlFriendApplication.baseAppDir + File.separator + srcPath
                    )
                )
            }
        }

        // 获取模型路径
        val basePath = finalModelList[1].characterVitsPath
        val rootFiles = getVITSModelFiles(basePath)
        // 导入配置
        vitsHelper.loadConfigs(rootFiles?.find { it.name.endsWith("json") }?.absolutePath) { isSuccess ->
            if (isSuccess) Logi("配置成功")
        }
        //导入模型
        vitsHelper.loadModel(rootFiles?.find { it.name.endsWith("bin") }?.absolutePath) { isSuccess ->
            if (isSuccess) Logi("模型导入成功")
        }
    }

    fun generateSound(msg: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // 推理生成语音
            vitsHelper.generateAndPlay(text = msg,
                targetSpeakerId = 0,
                callback = { isSuccess ->
                    Logi("generate sound $isSuccess")
                },
                forwardResult = {}
            )
        }
    }

    data class ModelList(
        // 角色名称
        var characterName: String,
        // vits模型路径
        var characterVitsPath: String = ""
    )

    fun getVITSModelFiles(basePath: String): List<File>? {
        val files = File(basePath).listFiles()
        if (!files.isNullOrEmpty()) {
            return files.toList()
        }
        return mutableListOf<File>()
    }

    private fun Logi(msg: String){
        Log.i("VITSGENERATION", msg)
    }
}