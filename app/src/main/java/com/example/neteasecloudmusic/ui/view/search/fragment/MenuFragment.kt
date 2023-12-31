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
import com.example.neteasecloudmusic.ui.view.search.adapter.MenuRecycleViewAdapter
import com.example.neteasecloudmusic.ui.view.songs.SongsActivity

class MenuFragment: BaseFragment() {
    private lateinit var context: Context
    private lateinit var recyclerView: RecyclerView
    private lateinit var menuRecycleViewAdapter: MenuRecycleViewAdapter
    private val directoryList: ArrayList<MenuBean> = ArrayList()

    override fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.directory_layout, container, false)
        context = view.context
        recyclerView = view.findViewById(R.id.dir_recycleView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        menuRecycleViewAdapter = MenuRecycleViewAdapter(context, directoryList)
        recyclerView.adapter = menuRecycleViewAdapter
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
        return view
    }

    override fun setData(dataList: List<*>) {
        directoryList.clear()
        directoryList.addAll(dataList.filterIsInstance<MenuBean>() as ArrayList<MenuBean>)
        if (::menuRecycleViewAdapter.isInitialized) {
            menuRecycleViewAdapter = MenuRecycleViewAdapter(context, directoryList)
            recyclerView.adapter = menuRecycleViewAdapter
        }
    }
}