package com.gaoshiqi.chatgpt

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.LinkedList
import java.util.concurrent.TimeUnit

class ChatGPTNetService(val context: Context) {
    companion object {
        private const val TEST_MODE = false
        private const val TAG = "ChatGPTNetService"
        private const val CHATGPT_BASE_URL = "https://api.openai.com/"
        private const val CHATGPT_DEFAULT_PROXY_URL = "https://api.openai-proxy.com/"
        private const val TIME_OUT_SECOND = 100L
        private const val MAX_LIST_SIZE = 100 // 存储历史记录上限
    }

    private val interceptor = ChatGPTInterceptor() // 拦截器加api key
    private var systemRole: RequestMessageBean? = null
    private val sendMessageAssistantList: LinkedList<RequestMessageBean> = LinkedList()
    private lateinit var retrofit: Retrofit
    private lateinit var chatAPI: ChatGPTAPI

    // 创建新的OkHttpClient
    private fun createClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    // 更新Retrofit目标地址
    fun updateRetrofit(baseUrl: String?){
        val usedUrl = if (baseUrl.isNullOrBlank()) {
            CHATGPT_BASE_URL
        } else baseUrl
        Log.i(TAG, "Retrofit 目标地址更新：$usedUrl")

        retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(usedUrl)
            .client(createClient())
            .build()
        chatAPI = retrofit.create(ChatGPTAPI::class.java)
    }

    // 修改ChatGPT API Key
    fun setPrivateKey(key: String): Boolean {
        if (key.isNotBlank()) {
            interceptor.privateKey = key
            return true
        }
        return false
    }

    // 设置assistant回复列表内容
    fun setAssistantList(list: List<String>) {
        sendMessageAssistantList.clear()
        sendMessageAssistantList.addAll(list.map {
            RequestMessageBean(RequestMessageBean.ROLE_ASSISTANT, it)
        })
    }

    // 设置SystemRole消息内容
    fun setSystemRole(content: String) {
        sendMessageAssistantList.clear()
        systemRole = RequestMessageBean(
            RequestMessageBean.ROLE_SYSTEM,
            content
        )
    }

    fun sendChatMessage(msg: String, callback: (response: ChatGPTResponseData?) -> Unit) {
        if (TEST_MODE) {
            getResponseFromTest(msg, callback)
            return
        }
        // 添加历史记录
        val sendPart = sendMessageAssistantList + RequestMessageBean(
            RequestMessageBean.ROLE_USER,
            msg
        )
        // 添加system上下文消息
        val requestMsg = systemRole?.let { system ->
            listOf(system) + sendPart
        }?: sendPart

        // 发送请求
        val call = chatAPI.sendMsg(ChatGPTRequestData(messages = requestMsg).toRequestBody())
        // 处理回调函数
        call.enqueue(object : Callback<ChatGPTResponseData>{
            override fun onResponse(
                call: Call<ChatGPTResponseData>,
                response: Response<ChatGPTResponseData>
            ) {
                Log.d(TAG, "send $call, receive ${response.body()}")
                if (!response.isSuccessful || response.body() == null) {
                    val errorJson = response.errorBody()?.string()
                    Log.e(TAG, "error msg is $errorJson")
                    val errorMsg = Gson().fromJson(errorJson, ErrorBody::class.java).error?.message
                    callback.invoke(
                        ChatGPTResponseData(
                            errorMsg = errorMsg ?: errorJson
                        )
                    )
                    return
                }
                response.body()?.choices?.firstOrNull()?.message?.content?.let {
                    // 如果列表超出上限，移除一个
                    if (sendMessageAssistantList.size > MAX_LIST_SIZE) {
                        sendMessageAssistantList.removeFirst()
                    }
                    sendMessageAssistantList.add(
                        RequestMessageBean(
                            RequestMessageBean.ROLE_ASSISTANT,
                            it
                        )
                    )
                    callback.invoke(response.body())
                }
            }

            override fun onFailure(call: Call<ChatGPTResponseData>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(context, "GPT请求失败： ${t.message}", Toast.LENGTH_SHORT)
                callback.invoke(null)
            }

        })
    }

    // mock测试数据
    private fun getResponseFromTest(msg: String, callback: (response: ChatGPTResponseData?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(5000)
            val response = ChatGPTResponseData().apply {
                choices =
                    listOf(
                        ListBean(
                            message = ResponseInnerMessageBean(
                                role = "test",
                                content = "这是一段测试回复"
                            ),
                            finish_reason = "stop",
                            index = 0
                        )
                    )
            }
            callback.invoke(response)
        }
    }
}