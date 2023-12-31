package com.example.neteasecloudmusic.ui.view.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.ui.base.BaseRecyclerViewAdapter
import com.example.neteasecloudmusic.ui.base.BaseViewHolder
import com.example.neteasecloudmusic.ui.model.MusicBean

class MusicRecycleViewAdapter(): BaseRecyclerViewAdapter<MusicRecycleViewAdapter.MyViewHolder>() {
    private var musicOnClickListener: MusicRecycleViewAdapterOnClickListener? = null

    constructor(context: Context, dataList: ArrayList<*>): this() {
        this.context = context
        this.dataList = dataList
    }

    class MyViewHolder(itemView: View): BaseViewHolder(itemView) {
        fun setData(context: Context, view: View, musicBean: MusicBean) {
            // 加载图片
//            Glide.with(context)
//                .load("${HttpConfig.baseUrl}${musicBean.cover_image_path}")
//                .into() // 图片对象
            view.findViewById<TextView>(R.id.name).text = musicBean.music_name
            view.findViewById<TextView>(R.id.singer).text = musicBean.singer
            view.findViewById<TextView>(R.id.introduction).text = musicBean.introduction
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.music_elem_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        context?.let {
            (holder as MyViewHolder).setData(
                it,
                holder.itemView,
                dataList?.get(position) as MusicBean
            )
        }
        musicOnClickListener?.linearLayoutOnClickListener(holder.itemView.findViewById(R.id.linearLayout), dataList?.get(position)as MusicBean)
    }

    // 点击事件接口
    interface MusicRecycleViewAdapterOnClickListener {
        fun linearLayoutOnClickListener(view: View, musicBean: MusicBean)
    }

    fun setOnClick(musicOnClickListener: MusicRecycleViewAdapterOnClickListener) {
        this.musicOnClickListener = musicOnClickListener
    }
}