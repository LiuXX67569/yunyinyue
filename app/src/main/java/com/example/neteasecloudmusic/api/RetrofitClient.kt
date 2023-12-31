package com.example.neteasecloudmusic.api

import com.example.neteasecloudmusic.api.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val retrofit: Retrofit
    = Retrofit.Builder()
    .baseUrl(HttpConfig.servlet)
    .client(getOkHttpClient())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor())
            .build()
    }

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)
    fun create(): ApiService = retrofit.create(ApiService::class.java)
}