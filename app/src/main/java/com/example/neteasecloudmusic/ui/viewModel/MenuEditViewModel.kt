package com.example.neteasecloudmusic.ui.viewModel

import androidx.lifecycle.MutableLiveData
import com.example.neteasecloudmusic.ui.base.BaseViewModel
import com.example.neteasecloudmusic.ui.model.MenuBean

class MenuEditViewModel: BaseViewModel() {
    var flag = MutableLiveData<Int>()

    fun updateMenu(menuBean: MenuBean){
        launch({httpUtil.update(menuBean)}, flag, true)
    }
}