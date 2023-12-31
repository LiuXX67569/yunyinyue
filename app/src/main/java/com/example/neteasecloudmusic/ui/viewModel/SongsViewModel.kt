package com.example.neteasecloudmusic.ui.viewModel

import androidx.lifecycle.MutableLiveData
import com.example.neteasecloudmusic.ui.base.BaseViewModel
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.model.MusicBean

class SongsViewModel: BaseViewModel() {
    var musicListAll = MutableLiveData<List<MusicBean>>()
    var musicListShow = MutableLiveData<List<MusicBean>>()
    var menu = MutableLiveData<MenuBean>()
    var flag : Boolean = false
    var f = MutableLiveData<Int>()

    fun getMusicList(musicIdList: String, musicNum: Int) {
        launch({ httpUtil.getMusicByIdList(musicIdList, musicNum)}, musicListAll, true)
//        musicListShow.postValue(musicListAll.value)
    }

    fun searchMusic(searchString: String)
    {
        val currentList = musicListAll.value ?: return
        val result = currentList.filter {
            (it.music_name?.contains(searchString) ?: false) or (it.singer?.contains(searchString)
                ?: (false or (it.album?.contains(searchString) ?: false)))
        }
        musicListShow.postValue(result)
    }

    fun unLike(id:Int) {
        val current = menu.value ?: return
        val currentIdList : List<Int> = current.musicIdList?.split(",")?.map { it.toInt() } ?: return
        val result = currentIdList.filter { it != id }
        val r :String = result.joinToString(separator = ",") { it.toString() }
        current.musicIdList = r
        current.musicNum = result.size
        menu.postValue(current)
        launch({httpUtil.update(current)}, f, true)
    }

    fun delete(id: Int)
    {
        launch({httpUtil.deleteById(id)}, f, true)
    }
}