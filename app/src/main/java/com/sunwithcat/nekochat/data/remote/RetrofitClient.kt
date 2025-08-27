package com.sunwithcat.nekochat.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // API URL
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    // 日志拦截器
    private val loggingInterceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    // 创建OkHttp客户端
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // 懒加载创建Retrofit实例
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL) // 设置基础URL
                .client(httpClient) // 设置自定义的OkHttp客户端
                .addConverterFactory(GsonConverterFactory.create()) // 设置JSON转换器
                .build()
    }

    // 暴露获取ApiService实例的方法
    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }
}
