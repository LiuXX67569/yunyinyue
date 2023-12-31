package com.example.neteasecloudmusic.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.neteasecloudmusic.ui.base.BaseViewModel
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.model.MusicShowBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicShowViewModel: BaseViewModel() {
    var musicShowList = MutableLiveData<List<MusicShowBean>>()
    var numbers = MutableLiveData<List<Int>>()
    var flag = MutableLiveData<Int>()

    fun updateMenu(menuBean: MenuBean){
        launch({httpUtil.update(menuBean)}, flag, true)
    }


    fun initNumbers(list: String){
        val n:List<Int> = list.split(",").map { it.toInt() }
        if (numbers.value != n)
            numbers.postValue(n)
    }

    fun getMusicShowByName(name: String) {
        launch({ httpUtil.getMusicShowByName(name) }, musicShowList, true)
    }

    fun like(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val n: MusicShowBean = musicShowList.value?.first { it.pk_id == id } ?: return@launch
            if (!n.flagInMenu) {
                val currentList = numbers.value ?: emptyList()
                if (!currentList.contains(id)) {
                    val newList = currentList.toMutableList().apply {
                        add(id)
                        sort()
                    }
                    withContext(Dispatchers.Main) {
                        numbers.postValue(newList)
                    }
                }
            }
        }
    }

    fun initializeFlagInMenu() {
        val musicList = musicShowList.value ?: return
        val numberList = numbers.value ?: return

        // 使用 HashSet 提高查找效率
        val numberSet = numberList.toHashSet()

        // 遍历 musicList，并初始化 flagInMenu
        musicList.forEach { musicShowBean ->
            musicShowBean.flagInMenu = musicShowBean.pk_id in numberSet
        }

        // 更新 MutableLiveData
        musicShowList.postValue(musicList)
    }

    fun getIdListString(): String {
        val idList = numbers.value ?: return ""
        return idList.joinToString(separator = ",") { it.toString() }
    }

    fun getListSize() : Int {
        return numbers.value?.size ?: 0
    }

}