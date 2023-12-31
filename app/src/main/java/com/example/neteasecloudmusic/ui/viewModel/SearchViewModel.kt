package com.example.neteasecloudmusic.ui.viewModel

import androidx.lifecycle.MutableLiveData
import com.example.neteasecloudmusic.ui.base.BaseViewModel
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.model.MusicBean
import com.example.neteasecloudmusic.ui.model.SingerBean

class SearchViewModel: BaseViewModel() {
    var musicList = MutableLiveData<List<MusicBean>>()
    var singerList = MutableLiveData<List<SingerBean>>()
    var menuList = MutableLiveData<List<MenuBean>>()

    fun getMusicByName(name: String) {
        launch({ httpUtil.getMusicByName(name) }, musicList, true)
    }

    fun getSingerByName(name: String) {
        launch({ httpUtil.getSingerByName(name) }, singerList, true)
    }

    fun getMenuByMenuName(name: String) {
        launch({ httpUtil.getMenuByMenuName(name) }, menuList, true)
    }
}