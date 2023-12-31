package com.example.neteasecloudmusic.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.neteasecloudmusic.ui.base.BaseViewModel
import com.example.neteasecloudmusic.ui.model.UserInfoBean
import com.example.neteasecloudmusic.ui.model.VerifyStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel: BaseViewModel() {
    var verifyStatus = MutableLiveData<VerifyStatus>()
    var userInfo = MutableLiveData<UserInfoBean>()
    var time = MutableLiveData<Int>()

    fun requestCode(email: String) {
        launch({ httpUtil.requestCode(email) }, verifyStatus, true)
    }

    fun verifyCode(email: String, code: String) {
        launch({ httpUtil.verifyCode(email, code)}, userInfo, true)
    }

    // 等待验证，开始倒计时60秒
    fun onTime() {
        val tmp = 60
        time.value = tmp
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                countdown(tmp)
            }
        }
    }

    private suspend fun countdown(t: Int) {
        var tmp = t
        while (tmp > 0) {
            delay(1000)
            tmp--
            time.value = tmp
        }
    }
}