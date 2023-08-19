package com.gaoshiqi.chatgpt

import androidx.annotation.Keep
import com.gaoshiqi.chatgpt.ChatGPTData.MAX_GENERATE_LIMIT
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject


object ChatGPTData {
    const val MAX_TOKEN_LIMIT = 4096 //turbo 3.5 max context length
    const val MAX_GENERATE_LIMIT = 1000 // chat gpt response limit
    const val MAX_SEND_LIMIT = MAX_TOKEN_LIMIT - MAX_GENERATE_LIMIT //send message length limit
    const val DEFAULT_SYSTEM_ROLE_LIMIT = 100 //default system role token length
}

@Keep
data class ChatGPTRequestData(
    val model: String = DEFAULT_MODEL, // 使用的模型ID
    val temperature: Int = DEFAULT_TEMPERATURE, // 不确定性
    val max_tokens: Int = MAX_GENERATE_LIMIT, // 最大token限制
    val messages: List<RequestMessageBean> = emptyList() //包含迄今为止的对话的消息列表
) {
    companion object {
        private const val DEFAULT_MODEL = "gpt-3.5-turbo"
        private const val DEFAULT_TEMPERATURE = 1
    }

    fun toRequestBody(): RequestBody {
        return RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            JSONObject(Gson().toJson(this)).toString()
        )
    }
}
@Keep
data class RequestMessageBean(
    var role: String = ROLE_USER, // 消息作者的角色，system/user/assistant之一
    var content: String = "" // 消息的内容
){
    companion object {
        const val ROLE_SYSTEM = "system" // 向模型提供上下文信息和初始指令
        const val ROLE_USER = "user" // 就是爷
        const val ROLE_ASSISTANT = "assistant" // 代表基于ChatGPT模型的AI助手
    }
}

@Keep
data class ChatGPTResponseData(
    var id: String? = null,
    var `object`: String? = null,
    var created: Int? = null,
    var model: String? = null,
    var choices: List<ListBean>? = null,
    var usage: Usage? = null,
    var errorMsg: String? = null,
)

@Keep
data class ListBean(
    var message: ResponseInnerMessageBean?,
    var index: Int?,
    var finish_reason: String?
)

@Keep
data class ResponseInnerMessageBean(
    var role: String?,
    var content: String? // ChatGPT回复内容
)

@Keep
data class Usage(
    var prompt_tokens: Int?,
    val completion_tokens: Int?,
    val total_tokens: Int?
)

@Keep
data class ErrorBody(
    val error: ErrorInnerBody? = null
)

@Keep
data class ErrorInnerBody(
    var message: String? = null,
    var type: String? = null,
    var param: String? = null,
    var code: String? = null
)