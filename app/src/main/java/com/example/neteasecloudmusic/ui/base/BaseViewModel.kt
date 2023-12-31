package com.example.neteasecloudmusic.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neteasecloudmusic.utils.HttpUtil
import com.example.neteasecloudmusic.utils.error.ErrorResult
import com.example.neteasecloudmusic.utils.error.ErrorUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel: ViewModel() {
    val httpUtil by lazy { HttpUtil.getInstance() }
    var isShowLoading = MutableLiveData<Boolean>()
    var errorData = MutableLiveData<ErrorResult>()

    private fun showLoading() {
        isShowLoading.value = true
    }

    private fun dismissLoading() {
        isShowLoading.value = false
    }

    private fun showError(errorResult: ErrorResult) {
        errorData.value = errorResult
    }

    private fun error(errorResult: ErrorResult) {
        showError(ErrorResult(errorResult.code, errorResult.errMsg))
    }

    /**
     * 注意此方法传入的参数：api是以函数作为参数传入
     * api：即接口调用方法
     * error：可以理解为接口请求失败回调
     * ->数据类型，表示方法返回该数据类型
     * ->Unit，表示方法不返回数据类型
     */
    fun <T> launch(
        api: suspend CoroutineScope.() ->BaseResult<T>,//请求接口方法，T表示data实体泛型，调用时可将data对应的bean传入即可
        liveData: MutableLiveData<T>,
        isShowLoading: Boolean = false
    ) {
        if (isShowLoading) {
            showLoading()
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val result = api()
                    withContext(Dispatchers.Main) {
                        if (result.errorCode == 0) {
                            liveData.value = result.data
                        } else {
                            error(ErrorResult(result.errorCode, result.errorMsg))
                        }
                    }
                }
            } catch (e: Throwable) {
                error(ErrorUtil.getError(e))
            } finally {
                dismissLoading()
            }
        }
    }
}