package com.example.neteasecloudmusic.ui.base

open class BaseResult<T> {
    val errorMsg: String? = null
    val errorCode: Int = 0
    val data: T? = null
}