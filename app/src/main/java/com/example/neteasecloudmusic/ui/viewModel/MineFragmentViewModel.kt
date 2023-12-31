package com.example.neteasecloudmusic.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.neteasecloudmusic.ui.base.BaseViewModel
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.model.UserInfoBean
import com.example.neteasecloudmusic.utils.DatabaseUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MineFragmentViewModel: BaseViewModel() {
    var menuId = MutableLiveData<Int>()
    var menuList = MutableLiveData<List<MenuBean>>()

    fun addMenu(menuBean: MenuBean) {
        launch({ httpUtil.add(menuBean) }, menuId, true)
    }

    fun getMenuByMenuIdList(menuIdList: String) {
        launch({ httpUtil.getMenuByMenuIdList(menuIdList) }, menuList, true)
    }

    fun updateDatabase(context: Context, userInfoBean: UserInfoBean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                DatabaseUtil(context).update(userInfoBean)
            }
        }
    }
}