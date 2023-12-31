package com.example.neteasecloudmusic.ui.view.search.fragment

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.ui.base.BaseFragment
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.model.MusicBean
import com.example.neteasecloudmusic.ui.model.SingerBean
import com.example.neteasecloudmusic.ui.view.search.adapter.MenuRecycleViewAdapter
import com.example.neteasecloudmusic.ui.view.search.adapter.MusicRecycleViewAdapter
import com.example.neteasecloudmusic.ui.view.search.adapter.SingerRecycleViewAdapter
import com.example.neteasecloudmusic.ui.view.songs.SongsActivity
import com.example.neteasecloudmusic.utils.CommonUtil

class SearchAllFragment: BaseFragment() {
    private lateinit var context: Context
    private lateinit var view: View

    //音乐视图
    private lateinit var musicRecyclerView: RecyclerView
    private var musicBeanList: ArrayList<MusicBean> = ArrayList()
    private lateinit var musicRecycleViewAdapter: MusicRecycleViewAdapter
    //歌单视图
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var menuRecycleViewAdapter: MenuRecycleViewAdapter
    private val directoryList: ArrayList<MenuBean> = ArrayList()
    //歌手视图
    private lateinit var singerRecyclerView: RecyclerView
    private lateinit var singerRecycleViewAdapter: SingerRecycleViewAdapter
    private var singerBeanList: ArrayList<SingerBean> = ArrayList()

    override fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
        view = inflater.inflate(R.layout.search_all_layout, container, false)
        context = view.context
        initMusic()
        initMenu()
        initSinger()
        return view
    }

    fun setMusicData(dataList: List<*>) {
        musicBeanList.clear()
        if(dataList.size > 5){
            musicBeanList.addAll(dataList.subList(0,5).filterIsInstance<MusicBean>() as ArrayList<MusicBean>)
        }else{
            musicBeanList.addAll(dataList.filterIsInstance<MusicBean>() as ArrayList<MusicBean>)
        }
        if (::musicRecycleViewAdapter.isInitialized) {
            musicRecycleViewAdapter = MusicRecycleViewAdapter(context, musicBeanList)
            musicRecyclerView.adapter = musicRecycleViewAdapter
        }
    }

    private fun initMusic(){
        musicRecyclerView = view.findViewById(R.id.music_view)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        musicRecyclerView.layoutManager = layoutManager
        musicRecycleViewAdapter = MusicRecycleViewAdapter(context, musicBeanList)
        musicRecyclerView.adapter = musicRecycleViewAdapter
        musicRecycleViewAdapter.setOnClick(object:
            MusicRecycleViewAdapter.MusicRecycleViewAdapterOnClickListener {
            override fun linearLayoutOnClickListener(view: View, musicBean: MusicBean) {
                view.setOnClickListener {
                    CommonUtil.toast(context, musicBean.toString())
                }
            }
        })
    }

    fun setMenuData(dataList: List<*>){
        directoryList.clear()
        if(dataList.size > 3){
            directoryList.addAll(dataList.subList(0,3).filterIsInstance<MenuBean>() as ArrayList<MenuBean>)
        }else{
            directoryList.addAll(dataList.filterIsInstance<MenuBean>() as ArrayList<MenuBean>)
        }
        if (::menuRecycleViewAdapter.isInitialized) {
            menuRecycleViewAdapter = MenuRecycleViewAdapter(context, directoryList)
            menuRecyclerView.adapter = menuRecycleViewAdapter
        }
    }

    private fun initMenu(){
        menuRecyclerView = view.findViewById(R.id.menu_view)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        menuRecyclerView.layoutManager = layoutManager
        menuRecycleViewAdapter = MenuRecycleViewAdapter(context, directoryList)
        menuRecyclerView.adapter = menuRecycleViewAdapter
        menuRecycleViewAdapter.setOnClick(object:
            MenuRecycleViewAdapter.MenuRecycleViewAdapterOnClickListener {
            override fun tvOnClickListener(view: View, menuBean: MenuBean) {
                view.setOnClickListener {
                    val intent = Intent(context, SongsActivity::class.java)
                    intent.putExtra("menuBean", menuBean)
                    startActivity(intent)
                }
            }
        })
    }

    fun setSingerData(dataList: List<*>){
        singerBeanList.clear()
        if(dataList.size > 3){
            singerBeanList.addAll(dataList.subList(0,3).filterIsInstance<SingerBean>() as ArrayList<SingerBean>)
        }else{
            singerBeanList.addAll(dataList.filterIsInstance<SingerBean>() as ArrayList<SingerBean>)
        }
        if (::singerRecycleViewAdapter.isInitialized) {
            singerRecycleViewAdapter = SingerRecycleViewAdapter(context, singerBeanList)
            singerRecyclerView.adapter = singerRecycleViewAdapter
        }
    }

    private fun initSinger(){
        singerRecyclerView = view.findViewById(R.id.singer_view)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        singerRecyclerView.layoutManager = layoutManager
        singerRecycleViewAdapter = SingerRecycleViewAdapter(context, singerBeanList)
        singerRecyclerView.adapter = singerRecycleViewAdapter
    }

    override fun setData(dataList: List<*>) {
//        TODO("Not yet implemented")
    }
}