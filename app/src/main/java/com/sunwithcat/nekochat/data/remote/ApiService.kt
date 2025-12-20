package com.sunwithcat.nekochat.data.remote

// API接口定义
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// 发送给AI的请求体的数据结构
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
)

data class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

data class Content(
    val parts: List<Part>,
    val role: String // 区分角色
)

data class Part(val text: String? = null, val inlineData: InlineData? = null)

data class InlineData(
    val mimeType: String, // 图片类型
    val data: String // 图片内容（经过 Base64 编码的字符串）
)

// AI返回的数据结构
data class GeminiResponse(
    val candidates: List<Candidate>,
)

data class Candidate(val content: Content)

interface ApiService {
    // gemini-2.5-flash
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Body geminiRequest: GeminiRequest,
        @Query("key") apiKey: String
    ): GeminiResponse
}
