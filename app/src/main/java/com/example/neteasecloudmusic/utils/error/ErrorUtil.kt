package com.example.neteasecloudmusic.utils.error

import retrofit2.HttpException

object ErrorUtil {
    fun getError(e: Throwable): ErrorResult {
        val errorResult = ErrorResult()
        if (e is HttpException) {
            errorResult.code = e.code()
        }
        errorResult.errMsg = e.message
        if (errorResult.errMsg.isNullOrEmpty()) {
            errorResult.errMsg = "网络请求失败，请重试"
        }
        return errorResult
    }
}