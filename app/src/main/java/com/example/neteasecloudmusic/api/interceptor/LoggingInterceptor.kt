package com.example.neteasecloudmusic.api.interceptor

import com.example.neteasecloudmusic.utils.LogUtil.debug
import com.example.neteasecloudmusic.utils.LogUtil.log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

class LoggingInterceptor: Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val httpUrl = request.url
        val response = chain.proceed(request)
        val requestBody = response.peekBody(1024 * 1024.toLong())
        if (httpUrl.toString().contains(".png") ||
            httpUrl.toString().contains(".jpg") ||
            httpUrl.toString().contains(".jpeg") ||
            httpUrl.toString().contains(".gif")) {
            return response
        }
        val result = requestBody.string()
        debug(result)
        return response
    }
}