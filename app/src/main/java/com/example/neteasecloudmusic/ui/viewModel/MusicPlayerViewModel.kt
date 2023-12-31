package com.example.neteasecloudmusic.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.neteasecloudmusic.ui.base.BaseViewModel
import com.example.neteasecloudmusic.ui.model.MusicBean
import com.example.neteasecloudmusic.utils.HttpUtil

class MusicPlayerViewModel: BaseViewModel() {
    init {
        Log.d("MusicPlayerViewModel", "ViewModel created")
    }
    var musicList = MutableLiveData<List<MusicBean>>()//音乐列表

    //获取所有音乐
    fun getAllMusic() {
        launch({ httpUtil.getAllMusic() }, musicList, true)
    }

    override fun onCleared() {
        Log.d("MusicPlayerViewModel", "ViewModel cleared")
        super.onCleared()
    }
}