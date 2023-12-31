package com.example.neteasecloudmusic.utils

import com.example.neteasecloudmusic.api.RetrofitClient
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.model.UserInfoBean
import okhttp3.MultipartBody
import retrofit2.http.Query

class HttpUtil {
    private val mService by lazy { RetrofitClient.create() }
    // 音乐服务器接口
    // 根据音乐名查找音乐
    suspend fun getMusicByName(name: String) = mService.getMusicByName(name)
    //查找所有音乐
    suspend fun getAllMusic() = mService.getAllMusic()
    // 根据音乐id列表获取音乐列表
    suspend fun getMusicByIdList(musicIdList: String, musicNum: Int)
            = mService.getMusicByIdList(musicIdList, musicNum)
    // 根据音乐文件地址下载音乐文件
    fun downloadMusic(@Query("path") path: String) = mService.downloadMusic(path)
    // 歌手服务器接口
    // 根据歌手名字查找歌手
    suspend fun getSingerByName(name: String) = mService.getSingerByName(name)
    // 歌单服务器接口
    // 根据歌单名查找歌单
    suspend fun getMenuByMenuName(name: String) = mService.getMenuByMenuName(name)
    //更新歌单信息
    suspend fun update(menu: MenuBean) = mService.update(menu)
    suspend fun getMusicShowByName(name: String) = mService.getMusicShowByName(name)
    //根据歌单id删除歌单
    suspend fun deleteById(songListId: Int) = mService.deleteById(songListId)
    //新增歌单
    suspend fun add(menu: MenuBean) = mService.add(menu)
    // 根据歌单id列表获取歌单
    suspend fun getMenuByMenuIdList(menuIdList: String) = mService.findMenuByMenuIdList(menuIdList)
    // 用户服务器接口
    // 获取验证码
    suspend fun requestCode(email: String) = mService.requestCode(email)
    // 验证验证码
    suspend fun verifyCode(email: String, code: String) = mService.verifyCode(email, code)
    // 更新用户信息
    suspend fun updateUserInfo(userInfo: UserInfoBean) = mService.updateUserInfo(userInfo)
    // 上传图片
    suspend fun upload(image: MultipartBody.Part) = mService.upload(image)

    companion object {
        @Volatile
        private var httpUtil: HttpUtil? = null

        fun getInstance() = httpUtil ?: synchronized(this) {
            httpUtil ?: HttpUtil().also { httpUtil = it }
        }
    }
}