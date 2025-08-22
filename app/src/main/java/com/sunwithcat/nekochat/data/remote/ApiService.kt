package com.sunwithcat.nekochat.data.remote

// API接口定义
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

// 发送给AI的请求体的数据结构
data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>,
    val role: String // 区分角色
)

data class Part(
    val text: String
)

// AI返回的数据结构
data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content
)


interface ApiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Body geminiRequest: GeminiRequest,
        @Query("key") apiKey: String
    ): GeminiResponse
}