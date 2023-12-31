package com.example.neteasecloudmusic.ui.view.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.neteasecloudmusic.ui.model.MusicBean

class MainSharedViewModel : ViewModel() {//播放器和播放列表共享当前音乐位置数据
    val currentMusicIndex = MutableLiveData<Int>()
    val musicList = MutableLiveData<List<MusicBean>>()

    fun getMusicList(): List<MusicBean>? {
        return musicList.value
    }

    fun updateMusicList(newMusicList: List<MusicBean>) {
        musicList.value = newMusicList
    }
}