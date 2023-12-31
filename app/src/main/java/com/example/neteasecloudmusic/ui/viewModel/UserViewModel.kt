package com.example.neteasecloudmusic.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.neteasecloudmusic.ui.base.BaseViewModel
import com.example.neteasecloudmusic.ui.model.UserInfoBean
import com.example.neteasecloudmusic.utils.DatabaseUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class UserViewModel: BaseViewModel() {
    var userInfo = MutableLiveData<UserInfoBean>()

    fun getUserInfo(context: Context, email: String) {
        viewModelScope.launch {
            userInfo.value = withContext(Dispatchers.IO) {
                DatabaseUtil(context).getUserInfo(email)
            }
        }
    }

    fun setUserInfo(userInfoBean: UserInfoBean) {
        userInfo.value = userInfoBean
    }

    var imagePath = MutableLiveData<String>()
    fun upload(image: MultipartBody.Part) {
        launch({ httpUtil.upload(image) }, imagePath, true)
    }

    var updateStatusS = MutableLiveData<Int>()
    fun updateUserInfo(userInfoBean: UserInfoBean) {
        launch({ httpUtil.updateUserInfo(userInfoBean) }, updateStatusS, true)
    }
    var updateStatusC = MutableLiveData<Int>()
    fun updateDatabase(context: Context, userInfoBean: UserInfoBean) {
        viewModelScope.launch {
            updateStatusC.value = withContext(Dispatchers.IO) {
                DatabaseUtil(context).update(userInfoBean)
            }
        }
    }
}