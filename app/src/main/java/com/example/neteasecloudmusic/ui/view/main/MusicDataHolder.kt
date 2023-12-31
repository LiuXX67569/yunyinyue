package com.example.neteasecloudmusic.ui.view.main

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neteasecloudmusic.ui.model.MusicBean

object MusicDataHolder {
    var musicList: ArrayList<MusicBean> = ArrayList()

    // 使用 MutableLiveData 包装 currentMusicIndex
    private val _currentMusicIndex = MutableLiveData<Int>()
    val currentMusicIndex: LiveData<Int> get() = _currentMusicIndex

    var mediaPlayerCurrentPosition: Int = 0
    lateinit var mediaPlayer: MediaPlayer

    //原始顺序副本
    var originalMusicList: ArrayList<MusicBean> = ArrayList()

    var isPlayingBeforeChange = false

    // 提供更新 currentMusicIndex 的方法
    fun updateCurrentMusicIndex(newIndex: Int) {
        _currentMusicIndex.value = newIndex
    }

}