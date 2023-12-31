package com.example.neteasecloudmusic.api

import com.example.neteasecloudmusic.ui.base.BaseResult
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.model.MusicBean
import com.example.neteasecloudmusic.ui.model.MusicShowBean
import com.example.neteasecloudmusic.ui.model.SingerBean
import com.example.neteasecloudmusic.ui.model.UserInfoBean
import com.example.neteasecloudmusic.ui.model.VerifyStatus
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

//API接口
interface ApiService {
    // 音乐服务器接口
    //查找所有音乐
    @GET("MusicServlet?action=findAll")
    suspend fun getAllMusic(): BaseResult<List<MusicBean>>
    // 根据音乐名查找音乐
    @GET("MusicServlet?action=findByName")
    suspend fun getMusicByName(@Query("name") name: String): BaseResult<List<MusicBean>>
    // 根据音乐id列表获取音乐列表
    @GET("MusicServlet?action=findByIdList")
    suspend fun getMusicByIdList(@Query("musicIdList") musicIdList: String,
                                 @Query("musicNum") musicNum: Int): BaseResult<List<MusicBean>>
    // 根据音乐文件地址下载音乐文件
    @GET("MusicServlet?action=downloadMusic")
    @Streaming
    fun downloadMusic(@Query("path") path: String): Call<ResponseBody>
    // 歌手服务器接口
    // 根据歌手名字查找歌手
    @GET("SingerServlet?action=findByName")
    suspend fun getSingerByName(@Query("name") name: String): BaseResult<List<SingerBean>>
    // 歌单服务器接口
    // 根据歌单名查找歌单
    @GET("MenuServlet?action=findByMenuName")
    suspend fun getMenuByMenuName(@Query("menu_name") name: String): BaseResult<List<MenuBean>>
    //更新歌单信息
    @POST("MenuServlet?action=update")
    suspend fun update(@Body menu: MenuBean) : BaseResult<Int>
    @GET("MusicServlet?action=findByName")
    suspend fun getMusicShowByName(@Query("name") name: String): BaseResult<List<MusicShowBean>>
    //根据歌单id删除歌单
    @GET("MenuServlet?action=deleteById")
    suspend fun deleteById(@Query("songListId") songListId: Int) : BaseResult<Int>
    //新增歌单
    @POST("MenuServlet?action=add")
    suspend fun add(@Body menu: MenuBean) : BaseResult<Int>
    // 根据歌单id列表获取歌单
    @GET("MenuServlet?action=findMenuByMenuIdList")
    suspend fun findMenuByMenuIdList(@Query("menuIdList") menuIdList: String): BaseResult<List<MenuBean>>
    // 用户服务器接口
    // 获取验证码
    @GET("UserInfoServlet?action=requestCode")
    suspend fun requestCode(@Query("email") email: String): BaseResult<VerifyStatus>
    // 验证验证码
    @GET("UserInfoServlet?action=verifyCode")
    suspend fun verifyCode(@Query("email") email: String,
                           @Query("code") code: String): BaseResult<UserInfoBean>
    // 更新用户信息
    @POST("UserInfoServlet?action=update")
    suspend fun updateUserInfo(@Body userInfo: UserInfoBean): BaseResult<Int>
    // 上传图片
    @POST("UserInfoServlet?action=upload")
    @Multipart
    suspend fun upload(@Part image: MultipartBody.Part): BaseResult<String>
}
